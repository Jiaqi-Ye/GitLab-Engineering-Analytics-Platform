import { createDemoAnalytics } from '../../_demo-data.js';

export default function handler(request, response) {
  if (request.method !== 'POST') {
    response.setHeader('Allow', 'POST');
    return response.status(405).json({ message: 'Method not allowed' });
  }

  response.setHeader('Cache-Control', 'no-store');
  return response.status(200).json(createDemoAnalytics());
}
