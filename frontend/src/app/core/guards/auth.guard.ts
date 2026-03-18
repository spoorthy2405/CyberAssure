import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {

    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.isLoggedIn()) {
        
        const roles = route.data['roles'] as Array<string>;
        
        if (roles && roles.length > 0) {
            const userRole = auth.getRole();
            if (userRole && !roles.includes(userRole)) {
                // User is logged in but doesn't have the required role
                router.navigate(['/']);
                return false;
            }
        }
        
        return true;
    }

    router.navigate(['/login']);
    return false;
};