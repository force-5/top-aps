// NOTE: from the excel file we have removed an entry on line 223 and line 264 as there were no corresponding Category entry
import com.force5solutions.care.cc.EntitlementPolicy
import com.force5solutions.care.aps.*

private final roleOwnerSlid = "OWNER-1"
private final gatekeeperSlid = "GATEKEEPER-1"
private final provisionerSlid = "PROVISIONER-1"
private final originName = "Manual"
private final boolean exposeInCentral = false;
private final entitlementPolicyName = "Physical";

String text = '''Apps,LFO PS BASIC PS EMPL ACCESS (AAM)
Apps,LFO PS COMM RM 2500 (CIP-AAM)
Apps,LFO PS DTS TRAINING ROOM (AAM)
Apps,LFO PS 2400 OFC AREA (AAM)
Apps,LFO PS HDWSH 2502 AREA (CIP-AAM)
Apps,LFO PS COMP OP CENTER (CIP-AAM)
Apps,LFO PS LFO CPU AREA (AAM)
Apps,LFO PS INTERIOR HDWSH 2502 AREA
Apps,CSE PS EBS ROOM (CIP-AAM)
Apps,LFO PS COMP RES STO RM (AAM)
Apps,LFO PS SCS PC TRAINING RM (AAM)
Apps,LFO PS SCS ROOM 1251 (CIP-AAM)
Apps,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Care Center Storm,LFO PS 3400 OFFICE AREA (AAM)
Care Center Storm,LFO PS MAP AREA (CIP-AAM)
Cleaners,LFO PS MAP AREA (CIP-AAM)
Cleaners,LFO PS LIMITED PS ACCESS (AAM)
Con,LFO PS LIMITED PS ACCESS (AAM)
Contr-CBRE,LFO PS ALL AREA ACCESS (CIP-AAM)
Contr-CBRE-CSE,CSE PS EBS ROOM (CIP-AAM)
Contr-Cleaners,LFO PS 2400 OFC AREA (AAM)
Contr-Cleaners,LFO PS LIMITED PS ACCESS (AAM)
Contr-Cleaners,LFO PS MAP AREA (CIP-AAM)
Contr-Cleaners,LFO PS COMM RM 2500 (CIP-AAM)
Contr-Cleaners,LFO PS COMP RES STO RM (AAM)
Contr-Cleaners,LFO PS HDWSH 2502 AREA (CIP-AAM)
Contr-Cleaners,LFO PS COMP OP CENTER (CIP-AAM)
Contr-Cleaners,LFO PS LFO CPU AREA (AAM)
Contr-DCSI,LFO PS 2400 OFC AREA (AAM)
Contr-DCSI,LFO PS COMP OP CENTER (CIP-AAM)
Contr-Edd Helms,LFO PS ALL AREA ACCESS (CIP-AAM)
Contr-EMC,LFO PS 2400 OFC AREA (AAM)
Contr-EMC,LFO PS COMP OP CENTER (CIP-AAM)
Contr-Facilities Supp,LFO PS ALL AREA ACCESS (CIP-AAM)
Contr-Facilities Supp CSE ONLY,CSE PS EBS ROOM (CIP-AAM)
Contr-FRCC,LFO PS BASIC PS EMPL ACCESS (AAM)
Contr-FRCC,LFO PS 2400 OFC AREA (AAM)
Contr-FRCC,LFO PS MAP AREA (CIP-AAM)
Contr-Gator Security,LFO PS BASIC PS EMPL ACCESS (AAM)
Contr-Gator Security,LFO PS 2400 OFC AREA (AAM)
Contr-GDKM,LFO PS 2400 OFC AREA (AAM)
Contr-GDKM,LFO PS COMP OP CENTER (CIP-AAM)
Contr-Gidant,LFO PS 3400 OFFICE AREA (AAM)
Contr-Gidant,LFO PS MAP AREA (CIP-AAM)
Contr-Gidant,LFO PS LFO CPU AREA (AAM)
Contr-Gidant,LFO PS COMM RM 2500 (CIP-AAM)
Contr-Gidant,LFO PS DTS TRAINING ROOM (AAM)
Contr-Gidant,LFO PS COMP OP CENTER (CIP-AAM)
Contr-Gidant,LFO PS 2400 OFC AREA (AAM)
Contr-Gidant/Bartec,LFO PS 2400 OFC AREA (AAM)
Contr-Intercon Systems,LFO PS ALL AREA ACCESS (CIP-AAM)
Contr-Meteorologist,LFO PS LIMITED PS ACCESS (AAM)
Contr-Meteorologist,LFO PS 2400 OFC AREA (AAM)
Contr-PC Supp,LFO PS LIMITED PS ACCESS (AAM)
Contr-PC Supp,LFO PS 2400 OFC AREA (AAM)
Contr-SCC Areva,LFO PS 2400 OFC AREA (AAM)
Contr-SCC Areva,LFO PS COMP OP CENTER (CIP-AAM)
Contr-SCC Supp,LFO PS BASIC PS EMPL ACCESS (AAM)
Contr-SCC Supp,LFO PS DTS TRAINING ROOM (AAM)
Contr-SCC Supp,LFO PS 2400 OFC AREA (AAM)
Contr-SCC Supp,LFO PS COMP OP CENTER (CIP-AAM)
Contr-SCC Supp,LFO PS MAP AREA (CIP-AAM)
Contr-Security,LFO PS ALL AREA ACCESS (CIP-AAM)
Contr-TELCO,LFO PS COMM RM 2500 (CIP-AAM)
Contr-TELCO,LFO PS 2400 OFC AREA (AAM)
Contr-TELCO,LFO PS 3400 OFFICE AREA (AAM)
Contr-TELCO,LFO PS 4400 OFFICE AREA (AAM)
Contr-Trane Co,LFO PS ALL AREA ACCESS (CIP-AAM)
Contr-UCI,LFO PS 3400 OFFICE AREA (AAM)
Contr-UCI,LFO PS 2400 OFC AREA (AAM)
Contr-UDS,LFO PS 2400 OFC AREA (AAM)
Contr-UDS,LFO PS 3400 OFFICE AREA (AAM)
DB,LFO PS BASIC PS EMPL ACCESS (AAM)
DB,LFO PS DTS TRAINING ROOM (AAM)
DB,LFO PS SCS PC TRAINING RM (AAM)
DB,LFO PS 2400 OFC AREA (AAM)
DB,LFO PS COMP OP CENTER (CIP-AAM)
DB,LFO PS MAP AREA (CIP-AAM)
Dir- Planning,LFO PS BASIC PS EMPL ACCESS (AAM)
Dir- Planning,LFO PS DTS TRAINING ROOM (AAM)
Dir- Planning,LFO PS 2400 OFC AREA (AAM)
Dir- Planning,LFO PS MAP AREA (CIP-AAM)
Dir-CCO,LFO PS BASIC PS EMPL ACCESS (AAM)
Dir-CCO,LFO PS 2400 OFC AREA (AAM)
Dir-CCO,LFO PS COMP OP CENTER (CIP-AAM)
Dir-CCO,LFO PS MAP AREA (CIP-AAM)
Distb,LFO PS SCS PC TRAINING RM (AAM)
Distb,LFO PS 2400 OFC AREA (AAM)
Distb,LFO PS COMP OP CENTER (CIP-AAM)
Distb DMS Support,LFO PS 2400 OFC AREA (AAM)
Distb DMS Support,LFO PS BASIC PS EMPL ACCESS (AAM)
Distb DMS Support,LFO PS COMP OP CENTER (CIP-AAM)
Distb DMS Support,LFO PS DTS TRAINING ROOM (AAM)
Distb DMS Support,LFO PS SCS PC TRAINING RM (AAM)
EBS,CSE PS EBS ROOM (CIP-AAM)
Executive,LFO PS 2400 OFC AREA (AAM)
Executive,LFO PS LIMITED PS ACCESS (AAM)
FPL Facilities Supp,LFO PS ALL AREA ACCESS (CIP-AAM)
FPL Security,LFO PS ALL AREA ACCESS (CIP-AAM)
FPL Security,CSE PS EBS ROOM (CIP-AAM)
FPL Security,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
FPL Security EBS,CSE PS EBS ROOM (CIP-AAM)
FPL Security LFO,LFO PS ALL AREA ACCESS (CIP-AAM)
FPL Security LFO,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
FPL TELCO,LFO PS 2400 OFC AREA (AAM)
FPL TELCO,LFO PS COMM RM 2500 (CIP-AAM)
FPL TELCO,LFO PS 3400 OFFICE AREA (AAM)
FPL TELCO,LFO PS 4400 OFFICE AREA (AAM)
FPL TELCO,LFO PS OBSERV DECK 4435 (AAM)
FPL TELCO,LFO PS MAP AREA (CIP-AAM)
FPL TELCO,LFO PS COMP OP CENTER (CIP-AAM)
HDW,CSE PS EBS ROOM (CIP-AAM)
HDW,LFO PS ALL AREA ACCESS (CIP-AAM)
HDW,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
IM Security,LFO PS 2400 OFC AREA (AAM)
IM Security,LFO PS 3400 OFFICE AREA (AAM)
Infrastructure,CSE PS EBS ROOM (CIP-AAM)
Infrastructure,LFO PS BASIC PS EMPL ACCESS (AAM)
Infrastructure,LFO PS COMM RM 2500 (CIP-AAM)
Infrastructure,LFO PS DTS TRAINING ROOM (AAM)
Infrastructure,LFO PS SCS PC TRAINING RM (AAM)
Infrastructure,LFO PS COMP RES STO RM (AAM)
Infrastructure,LFO PS 2400 OFC AREA (AAM)
Infrastructure,LFO PS HDWSH 2502 AREA (CIP-AAM)
Infrastructure,LFO PS COMP OP CENTER (CIP-AAM)
Infrastructure,LFO PS LFO CPU AREA (AAM)
Infrastructure,LFO PS MAP AREA (CIP-AAM)
Infrastructure,LFO PS COMPUTER LAB ACCESS (AAM)
Infrastructure,LFO PS SCS ROOM 1251 (CIP-AAM)
Infrastructure,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
LAN,LFO PS BASIC PS EMPL ACCESS (AAM)
LAN,LFO PS COMM RM 2500 (CIP-AAM)
LAN,LFO PS COMPUTER LAB ACCESS (AAM)
LAN,LFO PS DTS TRAINING ROOM (AAM)
LAN,LFO PS SCS PC TRAINING RM (AAM)
LAN,LFO PS 2400 OFC AREA (AAM)
LAN,LFO PS HDWSH 2502 AREA (CIP-AAM)
LAN,LFO PS COMP OP CENTER (CIP-AAM)
LAN,LFO PS LFO CPU AREA (AAM)
LAN,LFO PS MAP AREA (CIP-AAM)
LAN,LFO PS SCS ROOM 1251 (CIP-AAM)
Load Dispatchers,CSE PS EBS ROOM (CIP-AAM)
Load Dispatchers,LFO PS BASIC PS EMPL ACCESS (AAM)
Load Dispatchers,LFO PS DTS TRAINING ROOM (AAM)
Load Dispatchers,LFO PS SCS PC TRAINING RM (AAM)
Load Dispatchers,LFO PS 2400 OFC AREA (AAM)
Load Dispatchers,LFO PS MAP AREA (CIP-AAM)
Load Dispatchers,LFO PS GEN SWGR ROOF AREA (AAM)
Load Dispatchers,LFO PS SCS ROOM 1251 (CIP-AAM)
Load Dispatchers,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Mail,LFO PS LIMITED PS ACCESS (AAM)
Mail,LFO PS 2400 OFC AREA (AAM)
OFFICE AREA,LFO PS 2400 OFC AREA (AAM)
OFFICE AREA,LFO PS 3400 OFFICE AREA (AAM)
OFFICE AREA,LFO PS 4400 OFFICE AREA (AAM)
Office Area Secure,LFO PS 2400 OFC AREA (AAM)
Office Area Secure,LFO PS 3400 OFFICE AREA (AAM)
Office Area Secure,LFO PS 4400 OFFICE AREA (AAM)
Office Area Secure,LFO PS COMP OP CENTER (CIP-AAM)
Office Area Secure,LFO PS COMM RM 2500 (CIP-AAM)
Office Area Secure,LFO PS MAP AREA (CIP-AAM)
Operations Engineering,LFO PS BASIC PS EMPL ACCESS (AAM)
Operations Engineering,LFO PS DTS TRAINING ROOM (AAM)
Operations Engineering,LFO PS 2400 OFC AREA (AAM)
Operations Engineering,LFO PS COMP OP CENTER (CIP-AAM)
Operations Engineering,LFO PS MAP AREA (CIP-AAM)
Planning,LFO PS BASIC PS EMPL ACCESS (AAM)
Planning,LFO PS DTS TRAINING ROOM (AAM)
Planning,LFO PS 2400 OFC AREA (AAM)
Planning,LFO PS MAP AREA (CIP-AAM)
Procurement,LFO PS LIMITED PS ACCESS (AAM)
Procurement,LFO PS 2400 OFC AREA (AAM)
Project Group,CSE PS EBS ROOM (CIP-AAM)
Project Group,LFO PS BASIC PS EMPL ACCESS (AAM)
Project Group,LFO PS COMM RM 2500 (CIP-AAM)
Project Group,LFO PS DTS TRAINING ROOM (AAM)
Project Group,LFO PS SCS PC TRAINING RM (AAM)
Project Group,LFO PS COMP RES STO RM (AAM)
Project Group,LFO PS 2400 OFC AREA (AAM)
Project Group,LFO PS HDWSH 2502 AREA (CIP-AAM)
Project Group,LFO PS COMP OP CENTER (CIP-AAM)
Project Group,LFO PS LFO CPU AREA (AAM)
Project Group,LFO PS MAP AREA (CIP-AAM)
PS Interchange,LFO PS BASIC PS EMPL ACCESS (AAM)
PS Interchange,LFO PS DTS TRAINING ROOM (AAM)
PS Interchange,LFO PS 2400 OFC AREA (AAM)
PS Interchange,LFO PS COMP OP CENTER (CIP-AAM)
PS Interchange,LFO PS MAP AREA (CIP-AAM)
PS Interchange EBS,CSE PS EBS ROOM (CIP-AAM)
PS Interchange EBS,LFO PS 2400 OFC AREA (AAM)
PS Interchange EBS,LFO PS BASIC PS EMPL ACCESS (AAM)
PS Interchange EBS,LFO PS COMP OP CENTER (CIP-AAM)
PS Interchange EBS,LFO PS DTS TRAINING ROOM (AAM)
PS Interchange EBS,LFO PS MAP AREA (CIP-AAM)
PS Management,CSE PS EBS ROOM (CIP-AAM)
PS Management,LFO PS ALL AREA ACCESS (CIP-AAM)
PS Management,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
PS Ops Mang,CSE PS EBS ROOM (CIP-AAM)
PS Ops Mang,LFO PS BASIC PS EMPL ACCESS (AAM)
PS Ops Mang,LFO PS GEN SWGR ROOF AREA (AAM)
PS Ops Mang,LFO PS DTS TRAINING ROOM (AAM)
PS Ops Mang,LFO PS 2400 OFC AREA (AAM)
PS Ops Mang,LFO PS COMP OP CENTER (CIP-AAM)
PS Ops Mang,LFO PS MAP AREA (CIP-AAM)
PS Ops Mang,LFO PS SCS PC TRAINING RM (AAM)
PS Ops Mang,LFO PS SCS ROOM 1251 (CIP-AAM)
PS Ops Mang,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
PS Staff Mang,CSE PS EBS ROOM (CIP-AAM)
PS Staff Mang,LFO PS BASIC PS EMPL ACCESS (AAM)
PS Staff Mang,LFO PS DTS TRAINING ROOM (AAM)
PS Staff Mang,LFO PS 2400 OFC AREA (AAM)
PS Staff Mang,LFO PS COMP OP CENTER (CIP-AAM)
PS Staff Mang,LFO PS MAP AREA (CIP-AAM)
PS Staff Mang,LFO PS SCS ROOM 1251 (CIP-AAM)
PS Staff Mang,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
PS-Staff,LFO PS BASIC PS EMPL ACCESS (AAM)
PS-Staff,LFO PS DTS TRAINING ROOM (AAM)
PS-Staff,LFO PS SCS PC TRAINING RM (AAM)
PS-Staff,LFO PS 2400 OFC AREA (AAM)
PS-Staff,LFO PS COMP OP CENTER (CIP-AAM)
PS-Staff,LFO PS MAP AREA (CIP-AAM)
Pwr Coordinator,CSE PS EBS ROOM (CIP-AAM)
Pwr Coordinator,LFO PS BASIC PS EMPL ACCESS (AAM)
Pwr Coordinator,LFO PS DTS TRAINING ROOM (AAM)
Pwr Coordinator,LFO PS SCS PC TRAINING RM (AAM)
Pwr Coordinator,LFO PS 2400 OFC AREA (AAM)
Pwr Coordinator,LFO PS MAP AREA (CIP-AAM)
Pwr Coordinator,LFO PS GEN SWGR ROOF AREA (AAM)
Pwr Coordinator,LFO PS SCS ROOM 1251 (CIP-AAM)
Pwr Coordinator,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Pwr Sys Apps,LFO PS BASIC PS EMPL ACCESS (AAM)
Pwr Sys Apps,LFO PS DTS TRAINING ROOM (AAM)
Pwr Sys Apps,LFO PS SCS PC TRAINING RM (AAM)
Pwr Sys Apps,LFO PS 2400 OFC AREA (AAM)
Pwr Sys Apps,LFO PS COMP OP CENTER (CIP-AAM)
Pwr Sys Apps,LFO PS MAP AREA (CIP-AAM)
SCADA,CSE PS EBS ROOM (CIP-AAM)
SCADA,LFO PS BASIC PS EMPL ACCESS (AAM)
SCADA,LFO PS COMM RM 2500 (CIP-AAM)
SCADA,LFO PS DTS TRAINING ROOM (AAM)
SCADA,LFO PS 2400 OFC AREA (AAM)
SCADA,LFO PS HDWSH 2502 AREA (CIP-AAM)
SCADA,LFO PS COMP OP CENTER (CIP-AAM)
SCADA,LFO PS LFO CPU AREA (AAM)
SCADA,LFO PS MAP AREA (CIP-AAM)
SCADA,LFO PS SCS PC TRAINING RM (AAM)
SCADA,LFO PS SCS ROOM 1251 (CIP-AAM)
SCADA,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
SCC Infrastructure Tech Serv,CSE PS EBS ROOM (CIP-AAM)
SCC Infrastructure Tech Serv,LFO PS 2400 OFC AREA (AAM)
SCC Infrastructure Tech Serv,LFO PS BASIC PS EMPL ACCESS (AAM)
SCC Infrastructure Tech Serv,LFO PS COMM RM 2500 (CIP-AAM)
SCC Infrastructure Tech Serv,LFO PS COMP OP CENTER (CIP-AAM)
SCC Infrastructure Tech Serv,LFO PS DTS TRAINING ROOM (AAM)
SCC Infrastructure Tech Serv,LFO PS HDWSH 2502 AREA (CIP-AAM)
SCC Infrastructure Tech Serv,LFO PS LFO CPU AREA (AAM)
SCC Infrastructure Tech Serv,LFO PS MAP AREA (CIP-AAM)
SCC Infrastructure Tech Serv,LFO PS SCS PC TRAINING RM (AAM)
SCC Infrastructure Tech Serv,LFO PS SCS ROOM 1251 (CIP-AAM)
SCC Infrastructure Tech Serv,LFO PS COMP RES STO RM (AAM)
SCC Infrastructure Tech Serv,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
SCC Infrastructure Tech Serv - Supv,CSE PS EBS ROOM (CIP-AAM)
SCC Infrastructure Tech Serv - Supv,LFO PS ALL AREA ACCESS (CIP-AAM)
SCC Infrastructure Tech Serv - Supv,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
SCC Supv,CSE PS EBS ROOM (CIP-AAM)
SCC Supv,LFO PS BASIC PS EMPL ACCESS (AAM)
SCC Supv,LFO PS COMM RM 2500 (CIP-AAM)
SCC Supv,LFO PS DTS TRAINING ROOM (AAM)
SCC Supv,LFO PS SCS PC TRAINING RM (AAM)
SCC Supv,LFO PS 2400 OFC AREA (AAM)
SCC Supv,LFO PS HDWSH 2502 AREA (CIP-AAM)
SCC Supv,LFO PS COMP OP CENTER (CIP-AAM)
SCC Supv,LFO PS LFO CPU AREA (AAM)
SCC Supv,LFO PS MAP AREA (CIP-AAM)
SCC Supv,LFO PS SCS ROOM 1251 (CIP-AAM)
SCC Supv,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Supv Hdw,CSE PS EBS ROOM (CIP-AAM)
Supv Hdw,LFO PS ALL AREA ACCESS (CIP-AAM)
Supv Hdw,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Supv Infrastructure,CSE PS EBS ROOM (CIP-AAM)
Supv Infrastructure,LFO PS BASIC PS EMPL ACCESS (AAM)
Supv Infrastructure,LFO PS COMM RM 2500 (CIP-AAM)
Supv Infrastructure,LFO PS DTS TRAINING ROOM (AAM)
Supv Infrastructure,LFO PS SCS PC TRAINING RM (AAM)
Supv Infrastructure,LFO PS COMP RES STO RM (AAM)
Supv Infrastructure,LFO PS 2400 OFC AREA (AAM)
Supv Infrastructure,LFO PS HDWSH 2502 AREA (CIP-AAM)
Supv Infrastructure,LFO PS LFO CPU AREA (AAM)
Supv Infrastructure,LFO PS MAP AREA (CIP-AAM)
Supv Infrastructure,LFO PS COMP OP CENTER (CIP-AAM)
Supv Infrastructure,LFO PS COMPUTER LAB ACCESS (AAM)
Supv Infrastructure,LFO PS SCS ROOM 1251 (CIP-AAM)
Supv Infrastructure,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Supv Project Group,CSE PS EBS ROOM (CIP-AAM)
Supv Project Group,LFO PS BASIC PS EMPL ACCESS (AAM)
Supv Project Group,LFO PS COMM RM 2500 (CIP-AAM)
Supv Project Group,LFO PS DTS TRAINING ROOM (AAM)
Supv Project Group,LFO PS SCS PC TRAINING RM (AAM)
Supv Project Group,LFO PS COMP RES STO RM (AAM)
Supv Project Group,LFO PS 2400 OFC AREA (AAM)
Supv Project Group,LFO PS HDWSH 2502 AREA (CIP-AAM)
Supv Project Group,LFO PS LFO CPU AREA (AAM)
Supv Project Group,LFO PS MAP AREA (CIP-AAM)
Supv Project Group,LFO PS COMP OP CENTER (CIP-AAM)
Supv Project Group,LFO PS SCS ROOM 1251 (CIP-AAM)
Sys Operator,LFO PS BASIC PS EMPL ACCESS (AAM)
Sys Operator,LFO PS GEN SWGR ROOF AREA (AAM)
Sys Operator,LFO PS DTS TRAINING ROOM (AAM)
Sys Operator,LFO PS SCS PC TRAINING RM (AAM)
Sys Operator,LFO PS 2400 OFC AREA (AAM)
Sys Operator,LFO PS COMP OP CENTER (CIP-AAM)
Sys Operator,LFO PS MAP AREA (CIP-AAM)
Sys Operator,CSE PS EBS ROOM (CIP-AAM)
Sys Operator,LFO PS SCS ROOM 1251 (CIP-AAM)
Sys Operator,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
Sys Operator - Sr Engineer,CSE PS EBS ROOM (CIP-AAM)
Sys Operator - Sr Engineer,LFO PS 2400 OFC AREA (AAM)
Sys Operator - Sr Engineer,LFO PS BASIC PS EMPL ACCESS (AAM)
Sys Operator - Sr Engineer,LFO PS COMP OP CENTER (CIP-AAM)
Sys Operator - Sr Engineer,LFO PS DTS TRAINING ROOM (AAM)
Sys Operator - Sr Engineer,LFO PS MAP AREA (CIP-AAM)
Sys Operator - Sr Engineer,LFO PS SCS ROOM 1251 (CIP-AAM)
TELCO FPL,LFO PS COMM RM 2500 (CIP-AAM)
Temp Badge,LFO PS 4400 OFFICE AREA (AAM)
Temp-Oper,LFO PS 2400 OFC AREA (AAM)
Temp-Oper,LFO PS 3400 OFFICE AREA (AAM)
Temp-Oper,LFO PS 4400 OFFICE AREA (AAM)
Temp-Oper,LFO PS MAP AREA (CIP-AAM)
TPDC,LFO PS 2400 OFC AREA (AAM)
TPDC,LFO PS 3400 OFFICE AREA (AAM)
TPDC,LFO PS 4400 OFFICE AREA (AAM)
TPDC,LFO PS OBSERV DECK 4435 (AAM)
TPDC,LFO PS COMPUTER LAB ACCESS (AAM)
TPDC Support,LFO PS 2400 OFC AREA (AAM)
TPDC Support,LFO PS 3400 OFFICE AREA (AAM)
TPDC Support,LFO PS 4400 OFFICE AREA (AAM)
TPDC Support,LFO PS COMPUTER LAB ACCESS (AAM)
TPDC Support,LFO PS OBSERV DECK 4435 (AAM)
TPDC Support,CSE PS EBS ROOM (CIP-AAM)
Transmission,LFO PS LIMITED PS ACCESS (AAM)
Transmission,LFO PS 2400 OFC AREA (AAM)
Transmission Planning,LFO PS BASIC PS EMPL ACCESS (AAM)
Transmission Planning,LFO PS DTS TRAINING ROOM (AAM)
Transmission Planning,LFO PS 2400 OFC AREA (AAM)
Transmission Planning,LFO PS MAP AREA (CIP-AAM)
Transmission Services,LFO PS DTS TRAINING ROOM (AAM)
Transmission Services,LFO PS 2400 OFC AREA (AAM)
Transmission Services,LFO PS MAP AREA (CIP-AAM)
Transmission Services,LFO PS BASIC PS EMPL ACCESS (AAM)
Transmission Storm Group,LFO PS OBSERV DECK 4435 (AAM)
Transmission Storm Group,LFO PS 2400 OFC AREA (AAM)
Transmission Storm Group,LFO PS LIMITED PS ACCESS (AAM)
VP,CSE PS EBS ROOM (CIP-AAM)
VP,LFO PS ALL AREA ACCESS (CIP-AAM)
VP,PDEL DADE INDUSTRIAL SC PSP ROOM  (CIP-AAM)
'''

String errorMessage = ""

if (!ApsPerson.findBySlid(roleOwnerSlid)) {
    errorMessage = "INVALID SLID specified for role owner"
    println errorMessage;
}

if (!ApsPerson.findBySlid(gatekeeperSlid)) {
    errorMessage = "INVALID SLID specified for gatekeeper"
    println errorMessage;
}
if (!ApsPerson.findBySlid(provisionerSlid)) {
    errorMessage = "INVALID SLID specified for provisioner"
    println errorMessage;
}

if (!Origin.findByName(originName)) {
    errorMessage = "INVALID Origin"
    println errorMessage;
}

if (!EntitlementPolicy.findByName(entitlementPolicyName)) {
    errorMessage = "INVALID Entitlement Policy"
    println errorMessage;
}

if (!errorMessage) {
    // Used for entitlement and entitlement role
    RoleOwner roleOwner = RoleOwner.findByPerson(ApsPerson.findBySlid(roleOwnerSlid))

    // Used for Entitlement Role
    Gatekeeper gatekeeper = Gatekeeper.findByPerson(ApsPerson.findBySlid(gatekeeperSlid))

    // Use for entitlement
    Provisioner provisioner = Provisioner.findByPerson(ApsPerson.findBySlid(provisionerSlid))

    Origin origin = Origin.findByName(originName)

    EntitlementPolicy entitlementPolicy = EntitlementPolicy.findByName(entitlementPolicyName)

    String roleName
    List<Entitlement> entitlements = []
    boolean firstPass = true

    text.splitEachLine(',') { tokens ->
        if (roleName != tokens[0]) {
            if (!firstPass) {
                createOrFindAnEntitlementRole(roleName, origin, gatekeeper, roleOwner, entitlements)
                entitlements = []
            }
            roleName = tokens[0]
            firstPass = false
        }
        Entitlement entitlement = createOrFindAnEntitlement(tokens[1], origin, entitlementPolicy, roleOwner, provisioner)
        entitlements.add(entitlement)
    }

    //For the last entitlement role in the string
    createOrFindAnEntitlementRole(roleName, origin, gatekeeper, roleOwner, entitlements)

    // For a special Entitlement Role with a comma in the entitlementName
    entitlements = []
    List<String> entitlementNames = ['LFO PS 2400 OFC AREA (AAM)', 'LFO PS LIMITED PS ACCESS (AAM)', 'LFO PS MAP AREA (CIP-AAM)']
    roleName = "VP  - OFC AREA, MAP"
    entitlementNames.each { entitlementName ->
        Entitlement entitlement = createOrFindAnEntitlement(entitlementName, origin, entitlementPolicy, roleOwner, provisioner)
        entitlements.add(entitlement)
    }
    createOrFindAnEntitlementRole(roleName, origin, gatekeeper, roleOwner, entitlements)
}

Entitlement createOrFindAnEntitlement(String name, Origin origin, EntitlementPolicy entitlementPolicy, RoleOwner roleOwner, Provisioner provisioner) {
    Entitlement entitlement = Entitlement.findByName(name)
    if (!entitlement) {
        println "Creating an entitlement with the name : ${name}"
        entitlement = new Entitlement(isExposed: false, name: name, alias: name, origin: origin, owner: roleOwner, provisioners: [provisioner], type: entitlementPolicy.id, isApproved: true).save([failOnError: true, flush: true])
    }
    return entitlement
}


public void createOrFindAnEntitlementRole(String name, Origin origin, Gatekeeper gatekeeper, RoleOwner roleOwner, List<Entitlement> entitlements) {
    EntitlementRole role = EntitlementRole.findByName(name)
    if (!role) {
        println " Creating an entitlement role with the name " + name + " and containing ${entitlements.size()} entitlements. (${entitlements})"
        println " ENTITLEMENTS: " + entitlements
        role = new EntitlementRole(name: name, gatekeepers: [gatekeeper], entitlements: entitlements, owner: roleOwner, isApproved: true, isExposed: false, isPropagated: true).save([failOnError: true, flush: true])
    }
}
