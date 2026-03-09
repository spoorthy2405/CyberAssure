import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-admin-profile',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="space-y-6 max-w-4xl mx-auto">
      <div class="flex items-center justify-between pb-4 border-b border-slate-700/50">
        <h1 class="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-emerald-400">
          My Profile</h1>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div class="col-span-1">
          <div class="bg-slate-800/60 p-6 rounded-2xl border border-slate-700/50 shadow-lg text-center">
            <div class="w-24 h-24 bg-blue-600 rounded-full mx-auto flex items-center justify-center text-3xl font-bold mb-4 shadow-xl shadow-blue-500/20">
              {{ initials }}
            </div>
            <h2 class="text-xl font-bold text-white">{{ name }}</h2>
            <p class="text-slate-400 text-sm mt-1">System Administrator</p>
            <div class="mt-6 pt-6 border-t border-slate-700/50">
              <p class="text-xs text-slate-500 uppercase tracking-wider font-semibold">Account Status</p>
              <span class="inline-block mt-2 px-3 py-1 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-full text-xs font-medium">Verified Active</span>
            </div>
          </div>
        </div>

        <div class="col-span-2 space-y-6">
          <div class="bg-slate-800/60 p-6 rounded-2xl border border-slate-700/50 shadow-lg">
            <h3 class="text-lg font-bold text-white mb-6">Personal details</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div class="space-y-2">
                <label class="text-sm font-medium text-slate-400">Full Name</label>
                <p class="text-white font-medium p-3 bg-slate-900/50 rounded-lg border border-slate-700">{{ name }}</p>
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium text-slate-400">Email Address</label>
                <p class="text-white font-medium p-3 bg-slate-900/50 rounded-lg border border-slate-700">{{ email }}</p>
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium text-slate-400">Role</label>
                <p class="text-white font-medium p-3 bg-slate-900/50 rounded-lg border border-slate-700">Administrator</p>
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium text-slate-400">Joined Date</label>
                <p class="text-white font-medium p-3 bg-slate-900/50 rounded-lg border border-slate-700">January 12, 2024</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class Profile implements OnInit {
    name: string = 'System Administrator';
    email: string = 'admin@cyberassure.com';
    initials: string = 'SA';

    ngOnInit() {
        const storedEmail = localStorage.getItem('email');
        if (storedEmail) {
            this.email = storedEmail;
            this.name = storedEmail === 'admin@cyberassure.com' ? 'System Administrator' : storedEmail.split('@')[0];
            this.initials = this.email.substring(0, 2).toUpperCase();
        }
    }
}
