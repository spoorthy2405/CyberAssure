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
            icon: '📋', title: 'Detailed Risk Profiling',
            desc: 'Submit your security infrastructure details and upload supporting proof documents for an accurate risk evaluation.'
        },
        {
            icon: '🧑‍💼', title: 'Expert Underwriting',
            desc: 'Our specialized underwriters personally review your assessments to craft precise, realistic terms rather than relying on rigid algorithms.'
        },
        {
            icon: '⚙️', title: 'Customizable Policy Terms',
            desc: 'Every policy can be calibrated with customized premiums, coverage amounts, and specific deductibles tailored to your exact profile.'
        },
        {
            icon: '🔍', title: 'Transparent Claims Process',
            desc: 'Submit incidents directly through your dashboard. Our dedicated claims officers investigate and process settlements seamlessly.'
        },
        {
            icon: '🛡️', title: 'Secure Customer Portal',
            desc: 'Manage your active subscriptions, track pending assessments, and review policy documentation in one centralized environment.'
        },
        {
            icon: '🤝', title: 'End-to-End Support',
            desc: 'From initial application to claim resolution, we provide structured, role-based workflows to protect your business operations.'
        }
    ];

    steps = [
        {
            num: '01', icon: '🔍', title: 'Submit Risk Details',
            desc: 'Create an account and submit your company\'s cyber risk assessment along with necessary proof documents.', tag: 'EASY SETUP'
        },
        {
            num: '02', icon: '📊', title: 'Underwriter Review',
            desc: 'Our expert underwriters analyze your unique operational profile to accurately gauge your coverage requirements.', tag: 'EXPERT REVIEW'
        },
        {
            num: '03', icon: '📋', title: 'Get Custom Terms',
            desc: 'Receive a finalized, personalized policy complete with customized premiums, limits, and tailored provisions.', tag: 'TAILORED'
        },
        {
            num: '04', icon: '🛡️', title: 'Subscribe & Protect',
            desc: 'Subscribe directly from your dashboard. If a breach occurs, file claims instantly for officer investigation.', tag: 'SECURE'
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