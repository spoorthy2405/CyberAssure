import { Routes } from '@angular/router';
import { AdminLayout } from '../../shared/layouts/admin-layout/admin-layout';
import { AdminDashboard } from './dashboard/admin-dashboard';

export const ADMIN_ROUTES: Routes = [

    {
        path: '',
        component: AdminLayout,
        children: [

            {
                path: 'dashboard',
                component: AdminDashboard
            },

            {
                path: 'staff',
                loadComponent: () =>
                    import('./staff/staff')
                        .then(m => m.Staff)
            },

            {
                path: 'underwriters',
                loadComponent: () =>
                    import('./underwriters/underwriters')
                        .then(m => m.Underwriters)
            },

            {
                path: 'claims-officers',
                loadComponent: () =>
                    import('./claims-officers/claims-officers')
                        .then(m => m.ClaimsOfficers)
            },

            {
                path: 'customers',
                loadComponent: () =>
                    import('./customers/customers')
                        .then(m => m.Customers)
            },

            {
                path: 'policies',
                loadComponent: () =>
                    import('./policies/policies')
                        .then(m => m.Policies)
            },

            {
                path: 'claims',
                loadComponent: () =>
                    import('./claims/claims')
                        .then(m => m.Claims)
            },

            {
                path: 'subscriptions',
                loadComponent: () =>
                    import('./subscriptions/subscriptions')
                        .then(m => m.AdminSubscriptions)
            },

            {
                path: 'risk-monitoring',
                loadComponent: () =>
                    import('./risk-monitoring/risk-monitoring')
                        .then(m => m.RiskMonitoring)
            },

            {
                path: 'analytics',
                loadComponent: () =>
                    import('./analytics/analytics')
                        .then(m => m.Analytics)
            },

            {
                path: 'profile',
                loadComponent: () =>
                    import('./profile/profile')
                        .then(m => m.Profile)
            },
            {
                path: 'settings',
                loadComponent: () =>
                    import('./settings/settings')
                        .then(m => m.Settings)
            },

            // Legacy routes mapping to their new equivalents if any
            {
                path: 'users',
                redirectTo: 'customers',
                pathMatch: 'full'
            },
            {
                path: 'policy-plans',
                redirectTo: 'policies',
                pathMatch: 'full'
            },
            {
                path: 'assessments',
                redirectTo: 'risk-monitoring',
                pathMatch: 'full'
            },

            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            }

        ]
    }

];