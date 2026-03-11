import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [

    // LANDING PAGE
    {
        path: '',
        loadComponent: () =>
            import('./features/public/landing/landing')
                .then(m => m.Landing)
    },

    // LOGIN
    {
        path: 'login',
        loadComponent: () =>
            import('./features/public/login/login')
                .then(m => m.Login)
    },

    // REGISTER
    {
        path: 'register',
        loadComponent: () =>
            import('./features/public/register/register')
                .then(m => m.Register)
    },

    // CUSTOMER AREA
    {
        path: 'customer',
        canActivate: [authGuard],
        data: { roles: ['ROLE_CUSTOMER'] },
        loadChildren: () =>
            import('./features/customer/customer.routes')
                .then(m => m.CUSTOMER_ROUTES)
    },

    // ADMIN AREA
    {
        path: 'admin',
        canActivate: [authGuard],
        data: { roles: ['ROLE_ADMIN'] },
        loadChildren: () =>
            import('./features/admin/admin.routes')
                .then(m => m.ADMIN_ROUTES)
    },

    // UNDERWRITER AREA
    {
        path: 'underwriter',
        canActivate: [authGuard],
        data: { roles: ['ROLE_UNDERWRITER'] },
        loadChildren: () =>
            import('./features/underwriter/underwriter.routes')
                .then(m => m.UNDERWRITER_ROUTES)
    },

    // CLAIMS OFFICER AREA
    {
        path: 'claims',
        canActivate: [authGuard],
        data: { roles: ['ROLE_CLAIMS_OFFICER'] },
        loadChildren: () =>
            import('./features/claims/claims.routes')
                .then(m => m.CLAIMS_ROUTES)
    },
    {
        path: 'customer',
        loadChildren: () => import('./features/customer/customer.routes')
            .then(m => m.CUSTOMER_ROUTES)
    },

    // FALLBACK
    {
        path: '**',
        redirectTo: ''
    }

];