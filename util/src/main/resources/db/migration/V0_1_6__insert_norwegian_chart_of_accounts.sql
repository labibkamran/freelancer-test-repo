-- Insert Norwegian Standard Chart of Accounts NS 4102
-- Translated to English for international compatibility

-- 1 ASSETS
-- 10 Intangible assets etc.
INSERT INTO accounts (account_name, account_description, account_number, country_code, created_at, updated_at) VALUES
('Research and Development', 'Research and development costs', '1000', 'NO', NOW(), NOW()),
('Concessions', 'Concession rights and licenses', '1020', 'NO', NOW(), NOW()),
('Patents', 'Patent rights', '1030', 'NO', NOW(), NOW()),
('Licenses', 'License agreements', '1040', 'NO', NOW(), NOW()),
('Trademarks', 'Trademark rights', '1050', 'NO', NOW(), NOW()),
('Other Rights', 'Other intangible rights', '1060', 'NO', NOW(), NOW()),
('Deferred Tax Asset', 'Deferred tax benefits', '1070', 'NO', NOW(), NOW()),
('Goodwill', 'Goodwill from acquisitions', '1080', 'NO', NOW(), NOW()),

-- 11 Land, buildings and other real estate
('Buildings', 'Buildings and structures', '1100', 'NO', NOW(), NOW()),
('Building Installations', 'Building installations and improvements', '1120', 'NO', NOW(), NOW()),
('Assets Under Construction', 'Construction in progress', '1130', 'NO', NOW(), NOW()),
('Agricultural and Forest Properties', 'Farm and forest land', '1140', 'NO', NOW(), NOW()),
('Land and Ground Areas', 'Land and ground plots', '1150', 'NO', NOW(), NOW()),
('Residential Properties', 'Housing including land', '1160', 'NO', NOW(), NOW()),

-- 12 Transportation, inventory and machinery etc.
('Machinery and Equipment', 'Production machinery and equipment', '1200', 'NO', NOW(), NOW()),
('Machinery Under Construction', 'Machinery and equipment under installation', '1210', 'NO', NOW(), NOW()),
('Ships, Rigs, Aircraft', 'Marine vessels, rigs and aircraft', '1220', 'NO', NOW(), NOW()),
('Cars', 'Motor vehicles', '1230', 'NO', NOW(), NOW()),
('Other Transportation', 'Other transport equipment', '1240', 'NO', NOW(), NOW()),
('Furniture and Fixtures', 'Office furniture and fixtures', '1250', 'NO', NOW(), NOW()),
('Buildings Other Depreciation', 'Buildings with different depreciation schedules', '1260', 'NO', NOW(), NOW()),
('Tools etc.', 'Tools and similar equipment', '1270', 'NO', NOW(), NOW()),
('Office Machines', 'Office machines and computers', '1280', 'NO', NOW(), NOW()),

-- 13 Financial fixed assets
('Investments in Subsidiaries', 'Investments in subsidiary companies', '1300', 'NO', NOW(), NOW()),
('Investments Other Group Companies', 'Investments in other group companies', '1310', 'NO', NOW(), NOW()),
('Loans to Group Companies', 'Loans to companies in same group', '1320', 'NO', NOW(), NOW()),
('Investments in Associated Companies', 'Investments in associated companies', '1330', 'NO', NOW(), NOW()),
('Loans to Associated Companies', 'Loans to associated companies', '1340', 'NO', NOW(), NOW()),
('Investments in Shares and Assets', 'Investment in shares and other assets', '1350', 'NO', NOW(), NOW()),
('Bonds', 'Bond investments', '1360', 'NO', NOW(), NOW()),
('Receivables from Owners and Directors', 'Amounts owed by owners and board members', '1370', 'NO', NOW(), NOW()),
('Receivables from Employees', 'Amounts owed by employees', '1380', 'NO', NOW(), NOW()),
('Other Receivables', 'Other long-term receivables', '1390', 'NO', NOW(), NOW()),

-- 14 Inventory and advances to suppliers
('Raw Materials and Purchased Semi-finished', 'Raw materials and purchased components', '1400', 'NO', NOW(), NOW()),
('Work in Progress', 'Goods under development/production', '1420', 'NO', NOW(), NOW()),
('Finished Self-produced Goods', 'Completed manufactured products', '1440', 'NO', NOW(), NOW()),
('Purchased Goods for Resale', 'Inventory purchased for resale', '1460', 'NO', NOW(), NOW()),
('Advance Payments to Suppliers', 'Prepayments to suppliers', '1480', 'NO', NOW(), NOW()),

-- 15 Short-term receivables
('Accounts Receivable', 'Customer receivables', '1500', 'NO', NOW(), NOW()),
('Accrued Not Invoiced Revenue', 'Revenue earned but not yet invoiced', '1530', 'NO', NOW(), NOW()),
('Receivables from Group Companies', 'Customer receivables from group companies', '1550', 'NO', NOW(), NOW()),
('Other Receivables Group Companies', 'Other amounts owed by group companies', '1560', 'NO', NOW(), NOW()),
('Provision for Bad Debts', 'Allowance for doubtful accounts', '1580', 'NO', NOW(), NOW()),

-- 16 VAT, earned public grants etc.
('Outgoing VAT', 'VAT charged to customers', '1600', 'NO', NOW(), NOW()),
('Outgoing VAT High Rate', 'VAT charged at high rate (25%)', '1601', 'NO', NOW(), NOW()),
('Outgoing VAT Import Services', 'VAT on imported services', '1602', 'NO', NOW(), NOW()),
('Outgoing VAT Medium Rate', 'VAT charged at medium rate (15%)', '1603', 'NO', NOW(), NOW()),
('Outgoing VAT Low Rate', 'VAT charged at low rate (12%)', '1604', 'NO', NOW(), NOW()),
('Incoming VAT', 'VAT paid to suppliers', '1610', 'NO', NOW(), NOW()),
('Incoming VAT High Rate', 'VAT paid at high rate (25%)', '1611', 'NO', NOW(), NOW()),
('Incoming VAT Import Services', 'VAT paid on imported services', '1612', 'NO', NOW(), NOW()),
('Incoming VAT Medium Rate', 'VAT paid at medium rate (15%)', '1613', 'NO', NOW(), NOW()),
('Incoming VAT Low Rate', 'VAT paid at low rate (12%)', '1614', 'NO', NOW(), NOW()),
('Investment Tax', 'Investment tax liability', '1620', 'NO', NOW(), NOW()),
('Investment Tax Base', 'Basis for investment tax calculation', '1630', 'NO', NOW(), NOW()),
('VAT Settlement Account', 'VAT settlement with authorities', '1640', 'NO', NOW(), NOW()),
('Claims on Public Grants', 'Entitlement to public subsidies', '1670', 'NO', NOW(), NOW()),

-- 17 Prepaid expenses, accrued income etc.
('Prepaid Rent', 'Rent paid in advance', '1700', 'NO', NOW(), NOW()),
('Prepaid Interest', 'Interest paid in advance', '1710', 'NO', NOW(), NOW()),
('Accrued Rent', 'Rent income earned but not received', '1750', 'NO', NOW(), NOW()),
('Accrued Interest', 'Interest income earned but not received', '1760', 'NO', NOW(), NOW()),
('Share Capital Payment Claims', 'Claims for payment of share capital', '1780', 'NO', NOW(), NOW()),
('Interim Account', 'Temporary/suspense account', '1790', 'NO', NOW(), NOW()),

-- 18 Short-term financial investments
('Shares Group Companies', 'Shares in group companies', '1800', 'NO', NOW(), NOW()),
('Market-based Shares', 'Publicly traded shares', '1810', 'NO', NOW(), NOW()),
('Other Shares', 'Other share investments', '1820', 'NO', NOW(), NOW()),
('Market-based Bonds', 'Publicly traded bonds', '1830', 'NO', NOW(), NOW()),
('Other Bonds', 'Other bond investments', '1840', 'NO', NOW(), NOW()),
('Market-based Securities', 'Other publicly traded securities', '1850', 'NO', NOW(), NOW()),
('Other Certificates', 'Other certificate investments', '1860', 'NO', NOW(), NOW()),
('Other Market-based Financial Instruments', 'Other traded financial instruments', '1870', 'NO', NOW(), NOW()),
('Other Financial Instruments', 'Other financial instruments', '1880', 'NO', NOW(), NOW()),

-- 19 Bank deposits, cash and similar
('Cash', 'Physical cash', '1900', 'NO', NOW(), NOW()),
('Cash Register', 'Cash in register/till', '1910', 'NO', NOW(), NOW()),
('Bank Deposits', 'Money in bank accounts', '1920', 'NO', NOW(), NOW()),
('Bank Deposits Tax Withholding', 'Separate account for tax withholdings', '1950', 'NO', NOW(), NOW()),

-- 2 EQUITY AND DEBT

-- 20 Equity AS/ASA
('Share Capital', 'Issued share capital', '2000', 'NO', NOW(), NOW()),
('Treasury Shares', 'Own shares held by company', '2010', 'NO', NOW(), NOW()),
('Share Premium', 'Premium paid above par value', '2020', 'NO', NOW(), NOW()),
('Fund for Valuation Differences', 'Reserve for asset revaluations', '2040', 'NO', NOW(), NOW()),
('Other Equity', 'Other equity reserves', '2050', 'NO', NOW(), NOW()),
('Uncovered Loss', 'Accumulated uncovered losses', '2080', 'NO', NOW(), NOW()),

-- 21 Provisions for obligations
('Pension Obligations', 'Pension liabilities', '2100', 'NO', NOW(), NOW()),
('Deferred Tax', 'Deferred tax liability', '2120', 'NO', NOW(), NOW()),
('Unearned Revenue', 'Revenue received but not yet earned', '2160', 'NO', NOW(), NOW()),
('Other Provisions', 'Other provisions for liabilities', '2180', 'NO', NOW(), NOW()),

-- 22 Other long-term debt
('Convertible Loans', 'Convertible bonds/loans', '2200', 'NO', NOW(), NOW()),
('Bond Loans', 'Bond debt', '2210', 'NO', NOW(), NOW()),
('Debt to Credit Institutions', 'Bank loans and credit facilities', '2220', 'NO', NOW(), NOW()),
('Mortgage Loans', 'Secured loans/mortgages', '2240', 'NO', NOW(), NOW()),
('Debt to Group Companies', 'Long-term debt to group companies', '2260', 'NO', NOW(), NOW()),
('Other Foreign Currency Loans', 'Other loans in foreign currency', '2270', 'NO', NOW(), NOW()),
('Subordinated Loan Capital', 'Subordinated debt and capital', '2280', 'NO', NOW(), NOW()),

-- 23 Short-term convertible loans, bond loans and debt to credit institutions
('Short-term Convertible Loans', 'Short-term convertible debt', '2300', 'NO', NOW(), NOW()),
('Certificate Loans', 'Commercial paper and certificates', '2320', 'NO', NOW(), NOW()),
('Other Short-term Foreign Currency Loans', 'Short-term foreign currency debt', '2340', 'NO', NOW(), NOW()),
('Construction Loans', 'Loans for construction projects', '2360', 'NO', NOW(), NOW()),
('Overdraft Facility', 'Bank overdraft facility', '2380', 'NO', NOW(), NOW()),

-- 24 Accounts payable
('Accounts Payable', 'Supplier payables', '2400', 'NO', NOW(), NOW()),
('Accounts Payable Group Companies', 'Payables to group companies', '2460', 'NO', NOW(), NOW()),

-- 25 Payable tax
('Payable Tax Not Assessed', 'Tax liability not yet assessed', '2500', 'NO', NOW(), NOW()),
('Payable Tax Assessed', 'Tax liability assessed by authorities', '2510', 'NO', NOW(), NOW()),
('Tax Refund per Tax Act §31 5th paragraph', 'Special tax refund provision', '2530', 'NO', NOW(), NOW()),
('Advance Tax', 'Advance tax payments made', '2540', 'NO', NOW(), NOW()),

-- 26 Tax withholdings and other deductions
('Income Tax Withholding', 'Tax withheld from employee salaries', '2600', 'NO', NOW(), NOW()),
('Enforcement Withholding', 'Withholdings for debt collection', '2610', 'NO', NOW(), NOW()),
('Child Support Withholding', 'Child support withheld from salaries', '2620', 'NO', NOW(), NOW()),
('Social Security Withholding', 'Social security withheld from salaries', '2630', 'NO', NOW(), NOW()),
('Insurance Withholding', 'Insurance premiums withheld', '2640', 'NO', NOW(), NOW()),
('Union Dues Withholding', 'Union fees withheld from salaries', '2650', 'NO', NOW(), NOW()),

-- 27 Payable public taxes
('Outgoing VAT Payable', 'VAT owed to tax authorities', '2700', 'NO', NOW(), NOW()),
('Outgoing VAT High Rate Payable', 'VAT owed at high rate (25%)', '2701', 'NO', NOW(), NOW()),
('Outgoing VAT Import Services Payable', 'VAT owed on imported services', '2702', 'NO', NOW(), NOW()),
('Outgoing VAT Medium Rate Payable', 'VAT owed at medium rate (15%)', '2703', 'NO', NOW(), NOW()),
('Outgoing VAT Low Rate Payable', 'VAT owed at low rate (12%)', '2704', 'NO', NOW(), NOW()),
('Incoming VAT Deductible', 'VAT paid that can be deducted', '2710', 'NO', NOW(), NOW()),
('Incoming VAT High Rate Deductible', 'VAT paid at high rate (25%) - deductible', '2711', 'NO', NOW(), NOW()),
('Incoming VAT Import Services Deductible', 'VAT paid on imported services - deductible', '2712', 'NO', NOW(), NOW()),
('Incoming VAT Medium Rate Deductible', 'VAT paid at medium rate (15%) - deductible', '2713', 'NO', NOW(), NOW()),
('Incoming VAT Low Rate Deductible', 'VAT paid at low rate (12%) - deductible', '2714', 'NO', NOW(), NOW()),
('Investment Tax Payable', 'Investment tax owed', '2720', 'NO', NOW(), NOW()),
('Investment Tax Base Payable', 'Basis for investment tax owed', '2730', 'NO', NOW(), NOW()),
('VAT Settlement Account Payable', 'VAT settlement with authorities', '2740', 'NO', NOW(), NOW()),
('Payable Employer Social Security Tax', 'Employer social security tax owed', '2770', 'NO', NOW(), NOW()),
('Accrued Employer Social Security Tax', 'Accrued employer social security tax', '2780', 'NO', NOW(), NOW()),

-- 28 Dividends
('Declared Dividends', 'Dividends declared but not paid', '2800', 'NO', NOW(), NOW()),

-- 29 Other short-term debt
('Advances from Customers', 'Customer prepayments', '2900', 'NO', NOW(), NOW()),
('Debt to Employees and Owners', 'Amounts owed to employees and owners', '2910', 'NO', NOW(), NOW()),
('Debt to Group Companies', 'Short-term debt to group companies', '2920', 'NO', NOW(), NOW()),
('Salaries Payable', 'Unpaid salaries', '2930', 'NO', NOW(), NOW()),
('Holiday Pay Payable', 'Accrued holiday pay', '2940', 'NO', NOW(), NOW()),
('Accrued Interest', 'Interest expense accrued but not paid', '2950', 'NO', NOW(), NOW()),
('Accrued Expenses and Prepaid Income', 'Various accrued expenses', '2960', 'NO', NOW(), NOW()),
('Unearned Income', 'Revenue received but not yet earned', '2970', 'NO', NOW(), NOW()),
('Provisions and Obligations', 'Various provisions and obligations', '2980', 'NO', NOW(), NOW()),

-- 3 SALES AND OPERATING INCOME

-- 30 Sales income, taxable
('Sales Income Trading Goods Taxable High Rate', 'Sales of trading goods - high VAT rate', '3000', 'NO', NOW(), NOW()),
('Sales Income Self-produced Goods Taxable High Rate', 'Sales of manufactured goods - high VAT rate', '3010', 'NO', NOW(), NOW()),
('Sales Income Services Taxable High Rate', 'Sales of services - high VAT rate', '3020', 'NO', NOW(), NOW()),
('Sales Income Trading Goods Taxable Medium Rate', 'Sales of trading goods - medium VAT rate', '3030', 'NO', NOW(), NOW()),
('Sales Income Self-produced Goods Taxable Medium Rate', 'Sales of manufactured goods - medium VAT rate', '3040', 'NO', NOW(), NOW()),
('Sales Income Services Taxable Low Rate', 'Sales of services - low VAT rate', '3050', 'NO', NOW(), NOW()),
('Withdrawal of Goods Taxable High Rate', 'Personal use of goods - high VAT rate', '3060', 'NO', NOW(), NOW()),
('Withdrawal of Goods Taxable Medium Rate', 'Personal use of goods - medium VAT rate', '3063', 'NO', NOW(), NOW()),
('Withdrawal of Services Taxable High Rate', 'Personal use of services - high VAT rate', '3070', 'NO', NOW(), NOW()),
('Withdrawal of Services Taxable Low Rate', 'Personal use of services - low VAT rate', '3074', 'NO', NOW(), NOW()),
('Discounts and Sales Reductions Taxable', 'Discounts and sales reductions - taxable', '3080', 'NO', NOW(), NOW()),
('Refundable Expenses for Customer Account Taxable', 'Reimbursable expenses - taxable', '3090', 'NO', NOW(), NOW()),

-- 31 Sales income, tax-free
('Sales Income Trading Goods Tax-free', 'Sales of trading goods - VAT exempt', '3100', 'NO', NOW(), NOW()),
('Sales Income Self-produced Goods Tax-free', 'Sales of manufactured goods - VAT exempt', '3110', 'NO', NOW(), NOW()),
('Sales Income Services Tax-free', 'Sales of services - VAT exempt', '3120', 'NO', NOW(), NOW()),
('Withdrawal of Goods Tax-free', 'Personal use of goods - VAT exempt', '3160', 'NO', NOW(), NOW()),
('Discounts and Sales Reductions Tax-free', 'Discounts and sales reductions - VAT exempt', '3180', 'NO', NOW(), NOW()),
('Refundable Expenses for Customer Account Tax-free', 'Reimbursable expenses - VAT exempt', '3190', 'NO', NOW(), NOW()),

-- 32 Sales income, outside tax scope
('Sales Income Trading Goods Outside Tax Scope', 'Sales of trading goods - outside VAT scope', '3200', 'NO', NOW(), NOW()),
('Sales Income Self-produced Goods Outside Tax Scope', 'Sales of manufactured goods - outside VAT scope', '3210', 'NO', NOW(), NOW()),
('Sales Income Services Outside Tax Scope', 'Sales of services - outside VAT scope', '3220', 'NO', NOW(), NOW()),
('Withdrawal of Goods Outside Tax Scope', 'Personal use of goods - outside VAT scope', '3260', 'NO', NOW(), NOW()),
('Discounts and Sales Reductions Outside Tax Scope', 'Discounts and sales reductions - outside VAT scope', '3280', 'NO', NOW(), NOW()),

-- 33 Public taxes related to turnover
('Special Public Tax Manufactured/Sold Goods Taxable', 'Special taxes on manufactured/sold goods - taxable', '3300', 'NO', NOW(), NOW()),
('Special Public Tax Manufactured/Sold Goods Tax-free', 'Special taxes on manufactured/sold goods - tax-free', '3301', 'NO', NOW(), NOW()),
('Environmental Tax Manufactured/Sold Goods Taxable', 'Environmental taxes on goods - taxable', '3302', 'NO', NOW(), NOW()),
('Environmental Tax Manufactured/Sold Goods Tax-free', 'Environmental taxes on goods - tax-free', '3303', 'NO', NOW(), NOW()),

-- 34 Public grants/refunds
('Special Public Grant Manufactured/Sold Goods', 'Special public grants for manufactured/sold goods', '3400', 'NO', NOW(), NOW()),
('Special Public Grant Services', 'Special public grants for services', '3440', 'NO', NOW(), NOW()),

-- 35 Unearned income
('Warranty Income', 'Warranty service income', '3500', 'NO', NOW(), NOW()),
('Service Income', 'Service contract income', '3510', 'NO', NOW(), NOW()),

-- 36 Rental income
('Rental Income Real Estate', 'Income from real estate rentals', '3600', 'NO', NOW(), NOW()),
('Rental Income Other Fixed Assets', 'Income from equipment rentals', '3610', 'NO', NOW(), NOW()),
('Other Rental Income', 'Other rental income', '3620', 'NO', NOW(), NOW()),

-- 37 Commission income
('Commission Income', 'Commission and broker fees earned', '3700', 'NO', NOW(), NOW()),

-- 38 Gain on disposal of fixed assets
('Gain on Disposal of Fixed Assets', 'Profit from sale of fixed assets', '3800', 'NO', NOW(), NOW()),

-- 39 Other operating-related income
('Other Operating-related Income Taxable', 'Other operating income - taxable', '3900', 'NO', NOW(), NOW()),
('Outgoing Postage Taxable', 'Postage charged to customers - taxable', '3910', 'NO', NOW(), NOW()),
('Outgoing Fees Taxable', 'Fees charged to customers - taxable', '3920', 'NO', NOW(), NOW()),
('Other Operating-related Income Tax-free', 'Other operating income - tax-free', '3950', 'NO', NOW(), NOW()),
('Outgoing Postage Tax-free', 'Postage charged to customers - tax-free', '3960', 'NO', NOW(), NOW()),
('Outgoing Fees Tax-free', 'Fees charged to customers - tax-free', '3970', 'NO', NOW(), NOW()),

-- 4 COST OF GOODS

-- 40 Consumption of raw materials and purchased semi-finished products
('Purchase Raw Materials High Rate', 'Purchase of raw materials - high VAT rate', '4000', 'NO', NOW(), NOW()),
('Purchase Raw Materials Medium Rate', 'Purchase of raw materials - medium VAT rate', '4030', 'NO', NOW(), NOW()),
('Freight, Customs and Forwarding', 'Shipping, customs and forwarding costs', '4060', 'NO', NOW(), NOW()),
('Purchase Price Reduction', 'Discounts on purchases', '4070', 'NO', NOW(), NOW()),
('Inventory Change', 'Change in inventory levels', '4090', 'NO', NOW(), NOW()),

-- 41 Consumption of goods under production
('Purchase Goods Under Production High Rate', 'Purchase of work-in-progress - high VAT rate', '4100', 'NO', NOW(), NOW()),
('Purchase Goods Under Production Medium Rate', 'Purchase of work-in-progress - medium VAT rate', '4130', 'NO', NOW(), NOW()),
('Freight, Customs and Forwarding WIP', 'Shipping costs for work-in-progress', '4160', 'NO', NOW(), NOW()),
('Purchase Price Reduction WIP', 'Discounts on work-in-progress purchases', '4170', 'NO', NOW(), NOW()),
('Inventory Change WIP', 'Change in work-in-progress inventory', '4190', 'NO', NOW(), NOW()),

-- 42 Consumption of finished manufactured goods
('Purchase Finished Self-produced Goods High Rate', 'Purchase of finished goods - high VAT rate', '4200', 'NO', NOW(), NOW()),
('Purchase Finished Self-produced Goods Medium Rate', 'Purchase of finished goods - medium VAT rate', '4230', 'NO', NOW(), NOW()),
('Freight, Customs and Forwarding Finished', 'Shipping costs for finished goods', '4260', 'NO', NOW(), NOW()),
('Purchase Price Reduction Finished Taxable', 'Discounts on finished goods - taxable', '4270', 'NO', NOW(), NOW()),
('Inventory Change Finished', 'Change in finished goods inventory', '4290', 'NO', NOW(), NOW()),

-- 43 Consumption of purchased goods for resale
('Purchase Goods for Resale High Rate', 'Purchase of resale goods - high VAT rate', '4300', 'NO', NOW(), NOW()),
('Purchase Goods for Resale Medium Rate', 'Purchase of resale goods - medium VAT rate', '4330', 'NO', NOW(), NOW()),
('Freight, Customs and Forwarding Resale', 'Shipping costs for resale goods', '4360', 'NO', NOW(), NOW()),
('Purchase Price Reduction Resale', 'Discounts on resale goods', '4370', 'NO', NOW(), NOW()),
('Inventory Change Resale', 'Change in resale goods inventory', '4390', 'NO', NOW(), NOW()),

-- 45 External services and subcontracting
('External Services and Subcontracting', 'Outsourced services and subcontractor costs', '4500', 'NO', NOW(), NOW()),
('Inventory Change External', 'Inventory change for external services', '4590', 'NO', NOW(), NOW()),

-- 49 Other periodization
('Other Periodization', 'Other timing adjustments', '4900', 'NO', NOW(), NOW()),
('Inventory Change Other', 'Other inventory changes', '4990', 'NO', NOW(), NOW()),

-- 5 PAYROLL COSTS

-- 50 Salaries to employees
('Salaries to Employees', 'Employee salaries and wages', '5000', 'NO', NOW(), NOW()),
('Salary Accrual Account', 'Salary accruals and adjustments', '5090', 'NO', NOW(), NOW()),
('Calculated Holiday Pay', 'Accrued holiday pay liability', '5180', 'NO', NOW(), NOW()),
('Employer Social Security on Holiday Pay', 'Employer social security on accrued holiday pay', '5182', 'NO', NOW(), NOW()),

-- 52 Benefits in employment
('Company Car Benefit', 'Free use of company car', '5200', 'NO', NOW(), NOW()),
('Free Phone Benefit', 'Free telephone benefit', '5210', 'NO', NOW(), NOW()),
('Free Newspaper Benefit', 'Free newspaper/magazine benefit', '5220', 'NO', NOW(), NOW()),
('Free Lodging and Housing Benefit', 'Free accommodation benefit', '5230', 'NO', NOW(), NOW()),
('Interest Benefit', 'Benefit from low-interest loans', '5240', 'NO', NOW(), NOW()),
('Other Employment Benefits', 'Other benefits in kind', '5280', 'NO', NOW(), NOW()),
('Contra Account for Group 52', 'Offset account for benefits', '5290', 'NO', NOW(), NOW()),

-- 53 Other taxable compensation
('Bonus', 'Performance bonuses and incentives', '5300', 'NO', NOW(), NOW()),
('Board and Management Compensation', 'Compensation to board and management', '5330', 'NO', NOW(), NOW()),

-- 54 Employer social security tax and pension costs
('Employer Social Security Tax', 'Employer social security contributions', '5400', 'NO', NOW(), NOW()),
('Reportable Pension Costs', 'Pension costs subject to reporting', '5420', 'NO', NOW(), NOW()),

-- 55 Other cost compensation
('Other Cost Compensation', 'Other reimbursable employee costs', '5500', 'NO', NOW(), NOW()),

-- 56 Work compensation to owners etc.
('Work Compensation to Owners', 'Work compensation to owners in partnerships', '5600', 'NO', NOW(), NOW()),

-- 57 Public grants related to workforce
('Apprentice Grants', 'Government grants for apprentices', '5700', 'NO', NOW(), NOW()),

-- 58 Public refunds related to workforce
('Sick Pay Refunds', 'Government refunds for sick pay', '5800', 'NO', NOW(), NOW()),
('Employer Social Security Refunds', 'Refunds of employer social security', '5820', 'NO', NOW(), NOW()),

-- 59 Other personnel costs
('Gifts to Employees', 'Employee gifts and recognition', '5900', 'NO', NOW(), NOW()),
('Canteen Costs', 'Employee canteen/cafeteria costs', '5910', 'NO', NOW(), NOW()),
('Occupational Injury Insurance', 'Workplace injury insurance', '5920', 'NO', NOW(), NOW()),
('Other Insurance', 'Other employee-related insurance', '5930', 'NO', NOW(), NOW()),

-- 6 OTHER OPERATING COSTS, DEPRECIATION AND WRITE-DOWNS

-- 60 Depreciation and write-downs
('Depreciation Buildings and Real Estate', 'Depreciation of buildings and real estate', '6000', 'NO', NOW(), NOW()),
('Depreciation Transportation, Machinery and Inventory', 'Depreciation of equipment and machinery', '6010', 'NO', NOW(), NOW()),
('Depreciation Intangible Assets', 'Depreciation of intangible assets', '6020', 'NO', NOW(), NOW()),
('Write-down of Fixed Assets', 'Impairment of fixed assets', '6050', 'NO', NOW(), NOW()),

-- 61 Freight and transport costs related to sales
('Freight, Transport and Insurance', 'Shipping, transport and insurance costs', '6100', 'NO', NOW(), NOW()),
('Customs and Forwarding Costs', 'Customs and freight forwarding costs', '6110', 'NO', NOW(), NOW()),

-- 62 Energy, fuel and water for production
('Electricity', 'Electrical power costs', '6200', 'NO', NOW(), NOW()),
('Gas', 'Natural gas costs', '6210', 'NO', NOW(), NOW()),
('Heating Oil', 'Heating oil costs', '6220', 'NO', NOW(), NOW()),
('Coal, Coke', 'Coal and coke fuel costs', '6230', 'NO', NOW(), NOW()),
('Wood', 'Firewood costs', '6240', 'NO', NOW(), NOW()),
('Gasoline, Diesel Oil', 'Gasoline and diesel fuel', '6250', 'NO', NOW(), NOW()),
('Water', 'Water utility costs', '6260', 'NO', NOW(), NOW()),

-- 63 Premises costs
('Premises Rent', 'Rent for office/warehouse space', '6300', 'NO', NOW(), NOW()),
('Waste, Water, Sewage etc.', 'Utilities and waste management', '6320', 'NO', NOW(), NOW()),
('Light, Heat', 'Lighting and heating costs', '6340', 'NO', NOW(), NOW()),
('Cleaning', 'Cleaning services', '6360', 'NO', NOW(), NOW()),

-- 64 Rental of machinery, inventory etc.
('Machinery Rental', 'Rental of machinery and equipment', '6400', 'NO', NOW(), NOW()),
('Inventory Rental', 'Rental of furniture and fixtures', '6410', 'NO', NOW(), NOW()),
('Computer System Rental', 'Rental of computer systems', '6420', 'NO', NOW(), NOW()),
('Other Office Machine Rental', 'Rental of other office equipment', '6430', 'NO', NOW(), NOW()),
('Transportation Rental', 'Vehicle and transport equipment rental', '6440', 'NO', NOW(), NOW()),

-- 65 Tools, inventory and operating materials not to be capitalized
('Motor-driven Tools', 'Power tools and equipment', '6500', 'NO', NOW(), NOW()),
('Hand Tools', 'Manual tools and implements', '6510', 'NO', NOW(), NOW()),
('Auxiliary Tools', 'Supporting tools and accessories', '6520', 'NO', NOW(), NOW()),
('Special Tools', 'Specialized tools and equipment', '6530', 'NO', NOW(), NOW()),
('Inventory', 'Small inventory items', '6540', 'NO', NOW(), NOW()),
('Operating Materials', 'Operating supplies and materials', '6550', 'NO', NOW(), NOW()),
('Props', 'Props and accessories', '6560', 'NO', NOW(), NOW()),
('Work Clothes and Safety Equipment', 'Work clothing and safety gear', '6570', 'NO', NOW(), NOW()),

-- 66 Repair and maintenance
('Repair and Maintenance Buildings', 'Building repair and maintenance', '6600', 'NO', NOW(), NOW()),
('Repair and Maintenance Equipment', 'Equipment repair and maintenance', '6620', 'NO', NOW(), NOW()),

-- 67 External services
('Audit and Accounting Fees', 'Professional audit and accounting fees', '6700', 'NO', NOW(), NOW()),
('Legal and Economic Advisory Fees', 'Legal and financial consulting fees', '6720', 'NO', NOW(), NOW()),
('Other External Services', 'Other professional services', '6790', 'NO', NOW(), NOW()),

-- 68 Office costs, printed materials etc.
('Office Supplies', 'Office supplies and materials', '6800', 'NO', NOW(), NOW()),
('Printed Materials', 'Printing and stationery costs', '6820', 'NO', NOW(), NOW()),
('Newspapers, Magazines, Books etc.', 'Publications and reference materials', '6840', 'NO', NOW(), NOW()),
('Meetings, Courses, Training etc.', 'Training and professional development', '6860', 'NO', NOW(), NOW()),

-- 69 Telephone, postage etc.
('Telephone', 'Telephone and communication costs', '6900', 'NO', NOW(), NOW()),
('Postage', 'Postal and shipping costs', '6940', 'NO', NOW(), NOW()),

-- 7 OTHER OPERATING COSTS, DEPRECIATION AND WRITE-DOWNS

-- 70 Transportation costs
('Fuel', 'Vehicle fuel costs', '7000', 'NO', NOW(), NOW()),
('Maintenance', 'Vehicle maintenance and repair', '7020', 'NO', NOW(), NOW()),
('Insurance', 'Vehicle insurance', '7040', 'NO', NOW(), NOW()),

-- 71 Costs and compensation for travel, per diem, car etc.
('Car Allowance Taxable', 'Car mileage allowance - taxable', '7100', 'NO', NOW(), NOW()),
('Travel Costs Taxable', 'Travel expenses - taxable', '7130', 'NO', NOW(), NOW()),
('Travel Costs Non-taxable', 'Travel expenses - non-taxable', '7140', 'NO', NOW(), NOW()),
('Per Diem Costs Taxable', 'Daily allowances - taxable', '7150', 'NO', NOW(), NOW()),
('Per Diem Costs Non-taxable', 'Daily allowances - non-taxable', '7160', 'NO', NOW(), NOW()),

-- 72 Commission costs
('Commission Costs Taxable', 'Commission payments - taxable', '7200', 'NO', NOW(), NOW()),
('Commission Costs Non-taxable', 'Commission payments - non-taxable', '7210', 'NO', NOW(), NOW()),

-- 73 Sales, advertising and representation costs
('Sales Costs', 'Sales and marketing expenses', '7300', 'NO', NOW(), NOW()),
('Advertising Costs', 'Advertising and promotion costs', '7320', 'NO', NOW(), NOW()),
('Representation Deductible', 'Business entertainment - deductible', '7350', 'NO', NOW(), NOW()),
('Representation Non-deductible', 'Business entertainment - non-deductible', '7360', 'NO', NOW(), NOW()),

-- 74 Membership and gifts
('Membership Fees Deductible', 'Membership dues - deductible', '7400', 'NO', NOW(), NOW()),
('Membership Fees Non-deductible', 'Membership dues - non-deductible', '7410', 'NO', NOW(), NOW()),
('Gifts Deductible', 'Business gifts - deductible', '7420', 'NO', NOW(), NOW()),
('Gifts Non-deductible', 'Business gifts - non-deductible', '7430', 'NO', NOW(), NOW()),

-- 75 Insurance premiums, warranty and service costs
('Insurance Premiums', 'Business insurance premiums', '7500', 'NO', NOW(), NOW()),
('Warranty Costs', 'Product warranty costs', '7550', 'NO', NOW(), NOW()),
('Service Costs', 'Service and support costs', '7560', 'NO', NOW(), NOW()),

-- 76 License and patent costs
('License Fees and Royalties', 'License fees and royalty payments', '7600', 'NO', NOW(), NOW()),
('Patent Costs Own Patent', 'Costs related to own patents', '7610', 'NO', NOW(), NOW()),
('Trademark Costs etc.', 'Trademark and brand-related costs', '7620', 'NO', NOW(), NOW()),
('Control, Test and Stamp Fees', 'Inspection and certification fees', '7630', 'NO', NOW(), NOW()),

-- 77 Other costs
('Board and Company Assembly Meetings', 'Board meeting costs', '7700', 'NO', NOW(), NOW()),
('General Assembly', 'Shareholder meeting costs', '7710', 'NO', NOW(), NOW()),
('Costs for Treasury Shares', 'Costs related to own shares', '7730', 'NO', NOW(), NOW()),
('Rounding VAT Settlement', 'Rounding adjustments for VAT', '7740', 'NO', NOW(), NOW()),
('Rounding Taxable', 'Rounding adjustments - taxable', '7745', 'NO', NOW(), NOW()),
('Rounding Tax-free', 'Rounding adjustments - tax-free', '7746', 'NO', NOW(), NOW()),
('Property and Ground Rent Taxes', 'Property taxes and ground rent', '7750', 'NO', NOW(), NOW()),
('Bank and Card Fees', 'Banking and payment processing fees', '7770', 'NO', NOW(), NOW()),
('Interest and Collection Fees', 'Interest and debt collection fees', '7780', 'NO', NOW(), NOW()),
('Miscellaneous Costs', 'Other miscellaneous expenses', '7790', 'NO', NOW(), NOW()),

-- 78 Losses etc.
('Loss on Disposal of Fixed Assets', 'Loss from sale of fixed assets', '7800', 'NO', NOW(), NOW()),
('Recovery of Previously Written-down Receivables', 'Recovery of bad debts', '7820', 'NO', NOW(), NOW()),
('Loss on Receivables', 'Bad debt losses', '7830', 'NO', NOW(), NOW()),
('Loss on Contracts', 'Contract losses', '7860', 'NO', NOW(), NOW()),

-- 79 Accrual account
('Inventory Change Assets Under Construction', 'Change in assets under construction', '7900', 'NO', NOW(), NOW()),
('Obsolete Goods', 'Obsolete inventory write-offs', '7910', 'NO', NOW(), NOW()),

-- 8 FINANCIAL INCOME AND EXPENSES

-- 80 Financial income
('Income from Investment in Subsidiaries', 'Dividends from subsidiaries', '8000', 'NO', NOW(), NOW()),
('Income from Investment in Other Group Companies', 'Income from other group companies', '8010', 'NO', NOW(), NOW()),
('Income from Investment in Associated Companies', 'Income from associated companies', '8020', 'NO', NOW(), NOW()),
('Interest Income Group Companies', 'Interest from group companies', '8030', 'NO', NOW(), NOW()),
('Interest Income Tax-free', 'Tax-exempt interest income', '8040', 'NO', NOW(), NOW()),
('Other Interest Income', 'Other interest income', '8050', 'NO', NOW(), NOW()),
('Currency Gains', 'Foreign exchange gains', '8060', 'NO', NOW(), NOW()),
('Other Financial Income', 'Other financial income', '8070', 'NO', NOW(), NOW()),
('Value Increase Financial Current Assets', 'Appreciation of short-term investments', '8080', 'NO', NOW(), NOW()),

-- 81 Financial expenses
('Value Decrease Financial Current Assets', 'Depreciation of short-term investments', '8100', 'NO', NOW(), NOW()),
('Write-down Financial Current Assets', 'Write-down of short-term investments', '8110', 'NO', NOW(), NOW()),
('Write-down Financial Fixed Assets', 'Write-down of long-term investments', '8120', 'NO', NOW(), NOW()),
('Interest Expense Group Companies', 'Interest to group companies', '8130', 'NO', NOW(), NOW()),
('Interest Expense Non-deductible', 'Non-deductible interest expense', '8140', 'NO', NOW(), NOW()),
('Other Interest Expense', 'Other interest expenses', '8150', 'NO', NOW(), NOW()),
('Currency Losses', 'Foreign exchange losses', '8160', 'NO', NOW(), NOW()),
('Other Financial Expenses', 'Other financial expenses', '8170', 'NO', NOW(), NOW()),

-- 83 Tax expense on ordinary result
('Payable Tax', 'Current tax expense', '8300', 'NO', NOW(), NOW()),
('Deferred Tax', 'Deferred tax expense', '8320', 'NO', NOW(), NOW()),

-- 84 Extraordinary income
('Extraordinary Income', 'Extraordinary income items', '8400', 'NO', NOW(), NOW()),

-- 85 Extraordinary expenses
('Extraordinary Expenses', 'Extraordinary expense items', '8500', 'NO', NOW(), NOW()),

-- 86 Tax expense on extraordinary result
('Payable Tax Extraordinary Result', 'Tax on extraordinary items', '8600', 'NO', NOW(), NOW()),
('Deferred Tax Extraordinary Result', 'Deferred tax on extraordinary items', '8620', 'NO', NOW(), NOW()),

-- 88 Annual result
('Annual Result', 'Net income for the year', '8800', 'NO', NOW(), NOW()),

-- 89 Transfers and dispositions
('Transfer Fund Valuation Differences', 'Transfer to revaluation reserve', '8900', 'NO', NOW(), NOW()),
('Transfer Fund Joint Capital Same Company', 'Transfer to joint capital fund', '8910', 'NO', NOW(), NOW()),
('Declared Dividends/Interest Mutual Fund Certificates', 'Dividends and interest distributions', '8920', 'NO', NOW(), NOW()),
('Group Contribution', 'Group contribution payments', '8930', 'NO', NOW(), NOW()),
('Shareholder Contribution', 'Shareholder capital contributions', '8940', 'NO', NOW(), NOW()),
('Rights Issue', 'Share capital increases', '8950', 'NO', NOW(), NOW()),
('Transfer Other Equity', 'Transfer to other equity', '8960', 'NO', NOW(), NOW()),
('Uncovered Loss', 'Uncovered loss carried forward', '8990', 'NO', NOW(), NOW()); 