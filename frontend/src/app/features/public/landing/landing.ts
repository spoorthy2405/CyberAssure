import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-landing',
    imports: [CommonModule, RouterModule],
    templateUrl: './landing.html',
    styleUrl: './landing.css'
})
export class Landing implements OnInit, OnDestroy, AfterViewInit {

    attackCount = 1247;
    private counterInterval: any;
    activeIndustry = 'it';

    industries = [
        { id: 'it', label: 'IT & SaaS' },
        { id: 'finance', label: 'Finance & BFSI' },
        { id: 'health', label: 'Healthcare' },
        { id: 'ecom', label: 'E-commerce' },
        { id: 'mfg', label: 'Manufacturing' },
        { id: 'edu', label: 'Education' },
    ];

    industryData: Record<string, any> = {
        it: {
            icon: '💻', title: 'IT & SaaS',
            risks: ['Data breaches & API vulnerabilities',
                'Insider threats & credential theft',
                'Supply chain software attacks'],
            compliance: ['CERT-In Framework', 'DPDP Act 2023'],
            plan: 'Business Protector', planColor: 'blue'
        },
        finance: {
            icon: '🏦', title: 'Finance & BFSI',
            risks: ['Fraudulent transactions & fund theft',
                'Customer financial data exfiltration',
                'Regulatory non-compliance fines'],
            compliance: ['RBI Cyber Security Framework',
                'SEBI Guidelines', 'IRDAI Regulations'],
            plan: 'Enterprise Guard', planColor: 'purple'
        },
        health: {
            icon: '🏥', title: 'Healthcare',
            risks: ['Patient record theft & dark web sale',
                'Ransomware on hospital systems',
                'Medical device network compromise'],
            compliance: ['DPDP Act 2023', 'Clinical Data Regulations'],
            plan: 'Enterprise Guard', planColor: 'purple'
        },
        ecom: {
            icon: '🛒', title: 'E-commerce',
            risks: ['Payment fraud & card skimming',
                'Customer PII data breaches',
                'DDoS attacks during peak sales'],
            compliance: ['PCI-DSS Compliance', 'DPDP Act 2023'],
            plan: 'Business Protector', planColor: 'blue'
        },
        mfg: {
            icon: '🏭', title: 'Manufacturing',
            risks: ['Industrial control system attacks',
                'Supply chain compromise',
                'Intellectual property theft'],
            compliance: ['CERT-In Framework', 'ISO 27001'],
            plan: 'Basic Cyber Shield', planColor: 'green'
        },
        edu: {
            icon: '🎓', title: 'Education',
            risks: ['Student & faculty data breaches',
                'Ransomware on academic servers',
                'Phishing attacks on staff'],
            compliance: ['DPDP Act 2023', 'UGC Data Guidelines'],
            plan: 'Basic Cyber Shield', planColor: 'green'
        }
    };

    features = [
        {
            icon: '🎯', title: 'Precision Risk Assessment',
            desc: 'Not a generic checklist. Purpose-built for Indian business infrastructure, threat vectors, and regulatory landscape.'
        },
        {
            icon: '⚡', title: 'Sub-48hr Policy Approval',
            desc: 'Certified underwriters review and respond within 48 hours. No endless paperwork. No opaque delays.'
        },
        {
            icon: '📋', title: 'Industry-Tailored Coverage',
            desc: 'Plans calibrated to your sector — IT, BFSI, Healthcare, E-commerce. Generic policies leave dangerous gaps.'
        },
        {
            icon: '🔍', title: 'Real-Time Claim Transparency',
            desc: 'Track every claim milestone live. Full audit trail. No black-box processing.'
        },
        {
            icon: '🛡️', title: 'CERT-In & IRDAI Compliant',
            desc: 'Fully aligned with India\'s CERT-In cybersecurity framework and IRDAI regulations.'
        },
        {
            icon: '🤝', title: 'Dedicated Incident Officer',
            desc: 'One named claims officer personally assigned to every incident.'
        }
    ];

    steps = [
        {
            num: '01', icon: '🔍', title: 'Complete Risk Assessment',
            desc: 'Answer 20 targeted questions about your company, IT infrastructure, and security posture. Takes 10 minutes.', tag: '10 MIN'
        },
        {
            num: '02', icon: '📊', title: 'Receive Your Risk Score',
            desc: 'Our engine calculates a precise 0–100 cyber risk score and maps your exact vulnerability profile.', tag: 'INSTANT'
        },
        {
            num: '03', icon: '📋', title: 'Choose Your Policy',
            desc: 'Browse plans matched to your risk level. Underwriters approve within 48 hours.', tag: '48 HRS'
        },
        {
            num: '04', icon: '🛡️', title: 'Stay Protected & Claim',
            desc: 'Incident happens? Submit online. Your dedicated officer handles investigation and settlement.', tag: 'ALWAYS ON'
        }
    ];

    threats = [
        { text: 'Ransomware attack on logistics company', city: 'Mumbai', time: '2 hours ago' },
        { text: 'Data breach reported at fintech startup', city: 'Bangalore', time: '5 hours ago' },
        { text: 'DDoS attack on e-commerce platform', city: 'Delhi', time: '8 hours ago' },
        { text: 'Phishing campaign targeting BFSI sector', city: 'Hyderabad', time: '11 hours ago' },
    ];

    plans = [
        {
            tag: 'For Startups', name: 'Basic Cyber Shield',
            coverage: '₹50 Lakhs', highlight: false,
            included: ['Data Breach Response Costs', 'Basic Incident Support',
                'Legal Compliance Help', 'Email & Phone Support'],
            excluded: ['Ransomware Payment Coverage', 'Business Interruption Loss'],
            cta: 'Get Started', ctaRoute: '/register'
        },
        {
            tag: 'Most Popular', name: 'Business Protector',
            coverage: '₹2 Crore', highlight: true,
            included: ['Everything in Basic', 'Ransomware Coverage',
                'Business Interruption Loss', '48hr Claim Response SLA',
                'Forensic Investigation Costs'],
            excluded: ['Dedicated Account Manager'],
            cta: 'Get Started', ctaRoute: '/register'
        },
        {
            tag: 'For Enterprises', name: 'Enterprise Guard',
            coverage: '₹10 Crore', highlight: false,
            included: ['Everything in Pro', 'Full Incident Response Team',
                'Reinsurance Backed Coverage', 'Dedicated Account Manager',
                'Forensic Investigation', 'PR & Reputation Management'],
            excluded: [],
            cta: 'Contact Us', ctaRoute: '/register'
        }
    ];

    testimonials = [
        {
            quote: 'After our vendor\'s system was breached we had zero coverage. CyberAssure had us insured in 3 days and settled our claim in 12.',
            name: 'Rajesh Kumar', title: 'CTO, TechSol Solutions', city: 'Pune', stars: 5
        },
        {
            quote: 'As a healthcare company handling patient data, compliance was non-negotiable. CyberAssure\'s team understood our domain perfectly.',
            name: 'Priya Sharma', title: 'COO, MediCore Health', city: 'Bangalore', stars: 5
        },
        {
            quote: 'DDoS hit us during Diwali sale peak. CyberAssure covered our full business interruption loss completely.',
            name: 'Arjun Mehta', title: 'Founder, ShopEase', city: 'Mumbai', stars: 5
        }
    ];

    faqs = [
        {
            q: 'Do I need cyber insurance if I\'m a small company?',
            a: '43% of cyberattacks target SMBs. A single breach can exceed your annual revenue. Our Basic Cyber Shield is designed specifically for startups.', open: false
        },
        {
            q: 'How long does the risk assessment take?',
            a: 'Approximately 10 minutes. You answer targeted questions about company size, industry, IT infrastructure, and security practices.', open: false
        },
        {
            q: 'How fast are claims processed?',
            a: 'Claims officers begin review within 24 hours. Most claims with complete evidence are resolved within 7–14 business days.', open: false
        },
        {
            q: 'What incidents are covered?',
            a: 'Depends on your plan: data breaches, ransomware, DDoS business interruption, regulatory fines, forensic investigation, and third-party liability.', open: false
        },
        {
            q: 'Is my company\'s data safe with CyberAssure?',
            a: 'All data is AES-256 encrypted. We never share assessment data with third parties. Annual third-party security audits are conducted.', open: false
        },
        {
            q: 'Can I upgrade my plan after purchasing?',
            a: 'Yes. Upgrades can be requested anytime from your dashboard. Changes go through a revised assessment and underwriter approval within 48 hours.', open: false
        }
    ];

    get currentIndustry() {
        return this.industryData[this.activeIndustry];
    }

    ngOnInit() {
        this.counterInterval = setInterval(() => {
            this.attackCount += Math.floor(Math.random() * 3) + 1;
        }, 3000);
    }

    ngAfterViewInit() {
        // Small delay to ensure DOM is ready
        setTimeout(() => {
            this.initScrollAnimations();
        }, 100);
    }

    ngOnDestroy() {
        if (this.counterInterval) clearInterval(this.counterInterval);
    }

    initScrollAnimations() {
        const elements = document.querySelectorAll('.reveal');

        // Fallback — make all visible immediately if observer fails
        if (elements.length === 0) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(e => {
                if (e.isIntersecting) {
                    e.target.classList.add('visible');
                    observer.unobserve(e.target);
                }
            });
        }, { threshold: 0.05, rootMargin: '0px 0px -50px 0px' });

        elements.forEach(el => {
            // If element is already in viewport on load, show it
            const rect = el.getBoundingClientRect();
            if (rect.top < window.innerHeight) {
                el.classList.add('visible');
            } else {
                observer.observe(el);
            }
        });
    }

    setIndustry(id: string) { this.activeIndustry = id; }
    toggleFaq(i: number) { this.faqs[i].open = !this.faqs[i].open; }
    getStars(n: number): number[] { return Array(n).fill(0); }
}