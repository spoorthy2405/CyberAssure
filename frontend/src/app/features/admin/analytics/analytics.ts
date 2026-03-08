import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.html'
})
export class Analytics implements AfterViewInit {

  ngAfterViewInit(): void {

    new Chart("policiesChart", {
      type: 'bar',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
          label: 'Policies Sold',
          data: [120, 190, 300, 250, 220, 310],
          backgroundColor: '#3b82f6'
        }]
      }
    });

    new Chart("claimsChart", {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [{
          label: 'Claims Filed',
          data: [30, 50, 45, 60, 70, 90],
          borderColor: '#22c55e',
          fill: false
        }]
      }
    });

  }

}