import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminApiService } from '../services/admin.service';

@Component({
    selector: 'app-staff',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './staff.html'
})
export class Staff implements OnInit {

    showForm = false;
    staffMembers: any[] = [];

    newStaff = {
        fullName: '',
        email: '',
        password: '',
        roleName: '',
        department: 'CyberAssure Internal' // Default
    };

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.fetchStaff();
    }

    fetchStaff() {
        this.adminApi.getStaff().subscribe({
            next: (data) => this.staffMembers = data,
            error: (err) => console.error("Error fetching staff", err)
        });
    }

    toggleForm() {
        this.showForm = !this.showForm;
    }

    submitNewStaff() {
        this.adminApi.createStaff(this.newStaff).subscribe({
            next: (res) => {
                console.log('Staff created successfully', res);
                this.fetchStaff(); // Refresh the list
                this.toggleForm(); // Close the form
                // Reset form
                this.newStaff = { fullName: '', email: '', password: '', roleName: '', department: 'CyberAssure Internal' };
            },
            error: (err) => console.error("Error creating staff", err)
        });
    }

}
