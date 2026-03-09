import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-customer-sidebar',
    standalone: true,
    imports: [RouterLink, RouterLinkActive],
    templateUrl: './customer-sidebar.html'
})
export class CustomerSidebar {
    constructor(private authService: AuthService) { }
    logout() {
        if (confirm('Are you sure you want to log out?')) {
            this.authService.logout();
        }
    }
}
