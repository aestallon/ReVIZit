import {ApiError} from '../../api/revizit';
import {ToastMessageOptions} from 'primeng/api';

const ERROR_UNKNOWN: ApiError = {
  status: 500,
  timestamp: new Date().toISOString(),
  message: 'Unknown error',
  description: 'Error'
};
const isApiError: (it: any) => it is ApiError = (it: any): it is ApiError => {
  return 'status' in it && 'timestamp' in it && 'message' in it && 'description' in it;
}

export const toApiError: (error: any) => ApiError = (error: any) => {
  if (!error) {
    return ERROR_UNKNOWN;
  }

  if (isApiError(error)) {
    return error;
  }

  const inner = error.error;
  return toApiError(inner);
};

export const asErrorMsg = (error: any, summary?: string): ToastMessageOptions => {
  const apiError = toApiError(error);
  return {
    severity: 'error',
    summary: summary ?? 'Error',
    detail: apiError.message ?? apiError.description ?? 'Unknown error, please try later!',
    life: 3000,
  };
}
