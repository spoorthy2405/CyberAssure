import { Routes } from '@angular/router';

export const CUSTOMER_ROUTES: Routes = [

    {
        path: 'dashboard',
        loadComponent: () => import('./dashboard/customer-dashboard')
            .then(m => m.CustomerDashboard)
    },

    {
        path: 'risk',
        loadComponent: () => import('./risk-assessment/risk-assessment')
            .then(m => m.RiskAssessment)
    },

    {
        path: 'policies',
        loadComponent: () => import('./policies/policies')
            .then(m => m.Policies)
    },

    {
        path: 'subscriptions',
        loadComponent: () => import('./subscriptions/subscriptions')
            .then(m => m.Subscriptions)
    },

    {
        path: 'incidents',
        loadComponent: () => import('./incidents/incidents')
            .then(m => m.Incidents)
    },

    {
        path: 'claims',
        loadComponent: () => import('./claims/claims')
            .then(m => m.Claims)
    },

    {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
    }

];