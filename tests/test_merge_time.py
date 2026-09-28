"""Tests for the standalone merge request time-to-merge utility."""

from __future__ import annotations

from contextlib import redirect_stderr, redirect_stdout
import io
import json
from pathlib import Path
import tempfile
import unittest

from gitlab_analytics.merge_time import (
    MergeRequestDataError,
    calculate_merge_time,
    calculate_merge_times,
    main,
)


class MergeTimeTests(unittest.TestCase):
    def test_calculates_hours_for_z_timestamps(self) -> None:
        result = calculate_merge_time(
            {
                "iid": 12,
                "created_at": "2026-09-23T10:00:00Z",
                "merged_at": "2026-09-23T13:30:00Z",
            }
        )

        self.assertEqual(result, {"iid": 12, "merge_time_hours": 3.5})

    def test_handles_explicit_utc_offsets(self) -> None:
        result = calculate_merge_time(
            {
                "iid": 13,
                "created_at": "2026-09-23T10:00:00-05:00",
                "merged_at": "2026-09-23T18:30:00Z",
            }
        )

        self.assertEqual(result, {"iid": 13, "merge_time_hours": 3.5})

    def test_excludes_unmerged_records(self) -> None:
        results = calculate_merge_times(
            [
                {
                    "iid": 14,
                    "created_at": "2026-09-23T10:00:00Z",
                    "merged_at": None,
                },
                {
                    "iid": 15,
                    "created_at": "2026-09-23T10:00:00Z",
                    "merged_at": "2026-09-23T11:00:00Z",
                },
            ]
        )

        self.assertEqual(results, [{"iid": 15, "merge_time_hours": 1.0}])

    def test_rejects_non_array_input(self) -> None:
        with self.assertRaisesRegex(MergeRequestDataError, "JSON array"):
            calculate_merge_times({"iid": 16})

    def test_rejects_timestamp_without_utc_offset(self) -> None:
        with self.assertRaisesRegex(MergeRequestDataError, "UTC offset"):
            calculate_merge_time(
                {
                    "iid": 17,
                    "created_at": "2026-09-23T10:00:00",
                    "merged_at": "2026-09-23T11:00:00Z",
                }
            )

    def test_rejects_merge_before_creation(self) -> None:
        with self.assertRaisesRegex(MergeRequestDataError, "before created_at"):
            calculate_merge_time(
                {
                    "iid": 18,
                    "created_at": "2026-09-23T12:00:00Z",
                    "merged_at": "2026-09-23T11:00:00Z",
                }
            )

    def test_cli_outputs_json(self) -> None:
        records = [
            {
                "iid": 19,
                "created_at": "2026-09-23T10:00:00Z",
                "merged_at": "2026-09-23T12:15:00Z",
            }
        ]
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "merge_requests.json"
            path.write_text(json.dumps(records), encoding="utf-8")
            output = io.StringIO()
            with redirect_stdout(output):
                exit_code = main([str(path)])

        self.assertEqual(exit_code, 0)
        self.assertEqual(
            json.loads(output.getvalue()),
            [{"iid": 19, "merge_time_hours": 2.25}],
        )

    def test_cli_returns_error_for_invalid_json(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "merge_requests.json"
            path.write_text("not json", encoding="utf-8")
            error = io.StringIO()
            with redirect_stderr(error):
                exit_code = main([str(path)])

        self.assertEqual(exit_code, 1)
        self.assertIn("error: invalid JSON", error.getvalue())


if __name__ == "__main__":
    unittest.main()
