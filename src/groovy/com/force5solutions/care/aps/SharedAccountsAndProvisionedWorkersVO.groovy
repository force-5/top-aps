package com.force5solutions.care.aps

import com.force5solutions.care.cc.Worker

class SharedAccountsAndProvisionedWorkersVO {
    Entitlement entitlement
    List<String> sharedAccounts
    List<Worker> provisionedWorkers
}
