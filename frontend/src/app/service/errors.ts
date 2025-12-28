import {ApiError} from '../../api/revizit';
import {ToastMessageOptions} from 'primeng/api';

/*
export interface ApiError {
    status: number;
    timestamp: string;
    message?: string;
    description?: string;
}
 */

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

export const asErrorMsg = (error: any): ToastMessageOptions => {
  const apiError = toApiError(error);
  return {
    severity: 'error',
    summary: 'Error',
    detail: apiError.message ?? apiError.description ?? 'Unknown error, please try later!',
    life: 3000,
  };
}

export const asCustomErrorMsg = (error: any, summary: string): ToastMessageOptions => {
  const msg = asErrorMsg(error);
  return {...msg, summary};
}
