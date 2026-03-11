import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
//error interceptor globally handles authentication failures like 401 or 403 by clearing the session and redirecting the user to login."
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401 || error.status === 403) {
                // Clear local storage and enforce re-login
                localStorage.clear();
                router.navigate(['/login']);
            }

            // Re-throw so components can handle other errors (e.g. 400 Bad Request)
            return throwError(() => error);
        })
    );
};
