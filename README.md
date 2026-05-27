================================================================
   CamFlow v2.0 -- Campus Mobile Money & Savings System
   PKFokam Institute of Excellence
   Java Lab -- Mr. LELE Vaneck
   End of Semester Project -- Freshman 2 -- 2026
   Developer: NGASSA DJONGA Christian Fredy
================================================================

HOW TO RUN
----------
Windows (Recommended):  Double-click Run_CamFlow.bat
Any OS:                 java -jar CamFlow.jar
Compile from source:    javac -encoding UTF-8 -d out src/*.java
                        java -cp out CamFlowApp

================================================================
LOGIN CREDENTIALS
================================================================

--- SYSTEM ADMIN ACCOUNTS ---
Username : admin1   PIN : Admin1@2024   Name: Dr. KAMDEM Kamdem
Username : admin2   PIN : Admin2@2024   Name: Miss GRACE Grace

--- ACCOUNT HOLDERS ---
Account   Owner                 Status   Type  Balance     PIN
-----------------------------------------------------------------
CF-0001   NGASSA Fredy          STUDENT  SAV   5,000 FCFA  Ngassa@2024
CF-0002   KAZE Brayan           STUDENT  CUR  10,000 FCFA  Kaze@2024
CF-0003   ALIOU Maryam          STUDENT  SAV   7,500 FCFA  Aliou@2024
CF-0004   DIALLO Mouhamadou     STUDENT  CUR  15,000 FCFA  Diallo@2024
CF-0005   MVONDO Antony         STUDENT  SAV   3,000 FCFA  Mvondo@2024
CF-0006   TCHAMBA Prisca        STUDENT  SAV   8,000 FCFA  Tchamba@2024
CF-0007   NANVOU Sonia          STAFF    CUR   6,000 FCFA  Nanvou@2024
CF-0008   POKAM Paul            ADMIN    SAV  20,000 FCFA  Pokam@2024

================================================================
STATUS SYSTEM (NEW in v2.0)
================================================================
STUDENT       -- Own account only. Full self-service.
STAFF         -- Own account only. Same rules as Student.
ADMINISTRATOR -- All accounts. Open/close/manage any account.
                 Audit log stores last 15 actions.

================================================================
WHAT EACH ROLE CAN DO
================================================================
STUDENT / STAFF (own account):
  1. Transactions        (Deposit, Withdraw, Transfer)
  2. Transaction History (Last 5, Full table, Full receipts)
  3. My Profile
  4. Reports & Statistics (own account)
  5. Apply Monthly Interest (own Savings account)
  6. Change PIN
  7. Set Security Question
  + Audit log: last 5 actions tracked

ADMINISTRATOR account holder (any account):
  -- Everything above PLUS --
  1. Account Management  (Open, List, Search, Close)
  2. Transactions on any account
  3. History for any account
  4. System-wide Reports & Statistics
  5. Apply Monthly Interest to ALL Savings accounts
  + Audit log: last 15 actions tracked
  NOTE: Cannot close an account that still has money inside.

SYSTEM ADMIN (admin1 / admin2):
  Same capabilities as ADMINISTRATOR account holder.
  Authenticated by username + PIN (not account number).

================================================================
AUDIT LOG (NEW in v2.0)
================================================================
Every account records:
  LOGIN, LOGOUT, LOGIN_FAILED, PIN_CHANGE,
  SECURITY_UPDATED, ACCOUNT_OPENED, ACCOUNT_CLOSED,
  INTEREST_APPLIED
Stored with: who performed it + timestamp

================================================================
FEE RULES
================================================================
DEPOSIT    : No fee.
WITHDRAWAL : Fee = amount x 1.0%  (WAIVED if amount < 1,000 FCFA)
TRANSFER   : Fee = amount x 1.5%  (sender only, receiver gets full amount)
MIN BALANCE: 500 FCFA must always remain.
CLOSE RULE : Balance must be exactly 0 FCFA before closing.

================================================================
CHAPTERS COVERED
================================================================
Ch.1 -- Constants, Javadoc, main entry, welcome banner
Ch.2 -- Scanner, arithmetic, String.format, all data types
Ch.3 -- if/else, switch, while, for, do-while, nested loops, break
Ch.4 -- 12+ methods, value-returning, void, reusable validators
Ch.5 -- Account[], Transaction[][], AuditLog[], 2D arrays
Ch.6 -- OOP, private fields, getters/setters, static, constructors, this
================================================================
