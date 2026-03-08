import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

    const token = localStorage.getItem('token');

    // DO NOT attach token to login/register
    if (
        req.url.includes('/api/v1/auth/login') ||
        req.url.includes('/api/v1/auth/register')
    ) {
        return next(req);
    }

    if (token) {

        const authReq = req.clone({
            headers: req.headers.set(
                'Authorization',
                `Bearer ${token}`
            )
        });

        return next(authReq);
    }

    return next(req);
};