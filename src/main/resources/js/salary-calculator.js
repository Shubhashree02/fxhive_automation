document.addEventListener('DOMContentLoaded', function() {
    // Get all form elements
    const form = document.getElementById('salary-form');
    const grossInput = document.getElementById('gross');
    const basicInput = document.getElementById('basic');
    const hraInput = document.getElementById('hra');
    const allowanceInput = document.getElementById('allowance');
    const actualGrossInput = document.getElementById('actualGross');
    const pfEnableCheckbox = document.getElementById('pfEnable');
    const pfEmployeeInput = document.getElementById('pfEmployee');
    const pfEmployerInput = document.getElementById('pfEmployer');
    const esicEnableCheckbox = document.getElementById('esicEnable');
    const esicEmployeeInput = document.getElementById('esicEmployee');
    const esicEmployerInput = document.getElementById('esicEmployer');
    const incomeTaxInput = document.getElementById('incomeTax');
    const monthlyTaxSpan = document.getElementById('monthlyTax');

    // Constants for calculations
    const BASIC_PERCENTAGE = 0.40;
    const HRA_PERCENTAGE = 0.40;
    const PF_PERCENTAGE = 0.12;
    const ESIC_EMPLOYEE_PERCENTAGE = 0.0075;
    const ESIC_EMPLOYER_PERCENTAGE = 0.0325;

    // Function to calculate all salary components
    function calculateSalary() {
        const gross = parseFloat(grossInput.value) || 0;
        
        // Calculate Basic Pay (40% of Gross)
        const basic = gross * BASIC_PERCENTAGE;
        basicInput.value = basic.toFixed(2);
        
        // Calculate HRA (40% of Basic)
        const hra = basic * HRA_PERCENTAGE;
        hraInput.value = hra.toFixed(2);
        
        // Calculate Special Allowance
        const allowance = gross - (basic + hra);
        allowanceInput.value = allowance.toFixed(2);
        
        // Calculate PF if enabled
        let pfEmployee = 0;
        let pfEmployer = 0;
        if (pfEnableCheckbox.checked) {
            pfEmployee = basic * PF_PERCENTAGE;
            pfEmployer = basic * PF_PERCENTAGE;
        }
        pfEmployeeInput.value = pfEmployee.toFixed(2);
        pfEmployerInput.value = pfEmployer.toFixed(2);
        
        // Calculate ESIC if enabled
        let esicEmployee = 0;
        let esicEmployer = 0;
        if (esicEnableCheckbox.checked) {
            esicEmployee = gross * ESIC_EMPLOYEE_PERCENTAGE;
            esicEmployer = gross * ESIC_EMPLOYER_PERCENTAGE;
        }
        esicEmployeeInput.value = esicEmployee.toFixed(2);
        esicEmployerInput.value = esicEmployer.toFixed(2);
        
        // Calculate Actual Gross (Gross - Employer Contributions)
        const actualGross = gross - (pfEmployer + esicEmployer);
        actualGrossInput.value = actualGross.toFixed(2);
        
        // Update monthly tax display
        const monthlyTax = parseFloat(incomeTaxInput.value) || 0;
        monthlyTaxSpan.textContent = monthlyTax.toFixed(2);
    }

    // Add event listeners for real-time updates
    grossInput.addEventListener('input', calculateSalary);
    incomeTaxInput.addEventListener('input', calculateSalary);

    // Handle PF enable/disable
    pfEnableCheckbox.addEventListener('change', function() {
        if (this.checked) {
            // Once enabled, PF cannot be disabled
            this.disabled = true;
        }
        calculateSalary();
    });

    // Handle ESIC enable/disable
    esicEnableCheckbox.addEventListener('change', calculateSalary);

    // Form submission handler
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Validate required fields
        if (!grossInput.value || !document.getElementById('employee').value) {
            alert('Please fill in all required fields');
            return;
        }
        
        // Collect all salary data
        const salaryData = {
            employeeId: document.getElementById('employee').value,
            gross: parseFloat(grossInput.value),
            basic: parseFloat(basicInput.value),
            hra: parseFloat(hraInput.value),
            specialAllowance: parseFloat(allowanceInput.value),
            actualGross: parseFloat(actualGrossInput.value),
            pfEnabled: pfEnableCheckbox.checked,
            pfEmployee: parseFloat(pfEmployeeInput.value),
            pfEmployer: parseFloat(pfEmployerInput.value),
            esicEnabled: esicEnableCheckbox.checked,
            esicEmployee: parseFloat(esicEmployeeInput.value),
            esicEmployer: parseFloat(esicEmployerInput.value),
            professionalTax: parseFloat(document.getElementById('ptax').value) || 0,
            incomeTax: parseFloat(incomeTaxInput.value) || 0
        };
        
        // Here you would typically send the data to your backend
        console.log('Salary Data:', salaryData);
        
        // Show success message
        alert('Salary structure saved successfully!');
    });

    // Initialize calculations
    calculateSalary();
}); 