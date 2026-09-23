"""Calculate time-to-merge values from saved GitLab merge request JSON.

Run with::

    python3 -m gitlab_analytics.merge_time merge_requests.json

The input must be a JSON array. Each merged record must contain ``iid``,
``created_at``, and ``merged_at`` fields matching GitLab's merge request data.
Unmerged records, whose ``merged_at`` value is null, are omitted.
"""

from __future__ import annotations

import argparse
from datetime import datetime
import json
from pathlib import Path
import sys
from typing import Any, Sequence


class MergeRequestDataError(ValueError):
    """Raised when merge request input cannot produce a valid metric."""


def parse_gitlab_timestamp(value: Any, field_name: str) -> datetime:
    """Parse a timezone-aware ISO 8601 timestamp returned by GitLab."""
    if not isinstance(value, str) or not value:
        raise MergeRequestDataError(f"{field_name} must be a non-empty string")

    normalized = f"{value[:-1]}+00:00" if value.endswith("Z") else value
    try:
        timestamp = datetime.fromisoformat(normalized)
    except ValueError as exc:
        raise MergeRequestDataError(
            f"{field_name} is not a valid ISO 8601 timestamp: {value!r}"
        ) from exc

    if timestamp.tzinfo is None or timestamp.utcoffset() is None:
        raise MergeRequestDataError(f"{field_name} must include a UTC offset")
    return timestamp


def calculate_merge_time(record: Any) -> dict[str, int | float] | None:
    """Return an MR IID and elapsed hours, or ``None`` for an unmerged MR."""
    if not isinstance(record, dict):
        raise MergeRequestDataError("each merge request record must be an object")

    iid = record.get("iid")
    if not isinstance(iid, int) or isinstance(iid, bool):
        raise MergeRequestDataError("iid must be an integer")

    merged_at = record.get("merged_at")
    if merged_at is None:
        return None

    created = parse_gitlab_timestamp(record.get("created_at"), "created_at")
    merged = parse_gitlab_timestamp(merged_at, "merged_at")
    elapsed = merged - created
    if elapsed.total_seconds() < 0:
        raise MergeRequestDataError(
            f"merge request !{iid} has merged_at before created_at"
        )

    return {
        "iid": iid,
        "merge_time_hours": round(elapsed.total_seconds() / 3600, 2),
    }


def calculate_merge_times(records: Any) -> list[dict[str, int | float]]:
    """Calculate metrics for every merged record in a JSON-compatible list."""
    if not isinstance(records, list):
        raise MergeRequestDataError("input must be a JSON array")

    results = []
    for record in records:
        metric = calculate_merge_time(record)
        if metric is not None:
            results.append(metric)
    return results


def load_merge_requests(path: Path) -> Any:
    """Load JSON input from ``path`` and report readable data errors."""
    try:
        with path.open(encoding="utf-8") as input_file:
            return json.load(input_file)
    except OSError as exc:
        raise MergeRequestDataError(f"could not read {path}: {exc}") from exc
    except json.JSONDecodeError as exc:
        raise MergeRequestDataError(f"invalid JSON in {path}: {exc.msg}") from exc


def build_parser() -> argparse.ArgumentParser:
    """Build the command-line parser."""
    parser = argparse.ArgumentParser(
        description="Calculate time-to-merge hours from GitLab MR JSON."
    )
    parser.add_argument("json_file", type=Path, help="path to a GitLab MR JSON array")
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    """Run the command-line utility."""
    parser = build_parser()
    arguments = parser.parse_args(argv)

    try:
        records = load_merge_requests(arguments.json_file)
        results = calculate_merge_times(records)
    except MergeRequestDataError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 1

    json.dump(results, sys.stdout, indent=2)
    print()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
