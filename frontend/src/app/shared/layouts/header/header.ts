import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.html'
})
export class Header implements OnInit {

  currentUser: any = {
    name: 'Admin User',
    role: 'Administrator',
    initials: 'AU'
  };

  showDropdown = false;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    // Attempt to pull user info from localStorage if present
    const email = localStorage.getItem('email');
    if (email) {
      // Typically the name would be added to the token or login response, 
      // but if not, we show the email username.
      const namePart = email.split('@')[0];
      const formattedName = namePart.charAt(0).toUpperCase() + namePart.slice(1);

      this.currentUser = {
        name: email === 'admin@cyberassure.com' ? 'System Administrator' : formattedName,
        role: localStorage.getItem('role') === 'ROLE_ADMIN' ? 'Admin' : 'User',
        initials: email.substring(0, 2).toUpperCase()
      };
    }
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  logout() {
    if (confirm('Are you sure you want to log out?')) {
      this.authService.logout();
    }
  }

}