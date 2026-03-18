import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html'
})
export class Sidebar {

  constructor(private authService: AuthService) { }

  logout() {
    if (confirm('Are you sure you want to log out?')) {
      this.authService.logout();
    }
  }

}