import { Routes } from '@angular/router';
import { AdminLayout } from '../../shared/layouts/admin-layout/admin-layout';

export const ADMIN_ROUTES: Routes = [

    {
        path: '',
        component: AdminLayout,
        children: [

            {
                path: 'dashboard',
                loadComponent: () =>
                    import('./dashboard/admin-dashboard')
                        .then(m => m.AdminDashboard)
            },

            {
                path: 'users',
                loadComponent: () =>
                    import('./users/users')
                        .then(m => m.Users)
            },

            {
                path: 'policy-plans',
                loadComponent: () =>
                    import('./policy-plans/policy-plans')
                        .then(m => m.PolicyPlans)
            },

            {
                path: 'assessments',
                loadComponent: () =>
                    import('./assessments/assessments')
                        .then(m => m.Assessments)
            },

            {
                path: 'claims',
                loadComponent: () =>
                    import('./claims/claims')
                        .then(m => m.Claims)
            },

            {
                path: 'analytics',
                loadComponent: () =>
                    import('./analytics/analytics')
                        .then(m => m.Analytics)
            },

            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }

        ]
    }

];