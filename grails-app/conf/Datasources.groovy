import org.codehaus.groovy.grails.commons.ConfigurationHolder

datasources = {

    datasource(name: 'care', environments: ['development', 'production', 'qa', 'mssql', 'demo']) {
        domainClasses([com.force5solutions.care.workflow.CentralWorkflowTask,
                com.force5solutions.care.workflow.CentralWorkflowTaskPermittedSlid,
                com.force5solutions.care.workflow.AbortCentralWorkflow,
                com.force5solutions.care.cc.BusinessUnit,
                com.force5solutions.care.cc.BusinessUnitRequester,
                com.force5solutions.care.cc.CcEntitlement,
                com.force5solutions.care.cc.CcEntitlementRole,
                com.force5solutions.care.cc.EntitlementPolicy,
                com.force5solutions.care.cc.CcOrigin,
                com.force5solutions.care.cc.CcCustomProperty,
                com.force5solutions.care.cc.CcCustomPropertyValue,
                com.force5solutions.care.cc.Certification,
                com.force5solutions.care.cc.Course,
                com.force5solutions.care.cc.CertificationStatus,
                com.force5solutions.care.cc.Contractor,
                com.force5solutions.care.cc.ContractorSupervisor,
                com.force5solutions.care.cc.Employee,
                com.force5solutions.care.cc.EmployeeSupervisor,
                com.force5solutions.care.cc.Location,
                com.force5solutions.care.cc.LocationType,
                com.force5solutions.care.cc.PeriodUnit,
                com.force5solutions.care.cc.Person,
                com.force5solutions.care.cc.CentralDataFile,
                com.force5solutions.care.cc.TerminateForCause,
                com.force5solutions.care.cc.Vendor,
                com.force5solutions.care.cc.Worker,
                com.force5solutions.care.cc.WorkerCourse,
                com.force5solutions.care.cc.WorkerCertification,
                com.force5solutions.care.cc.WorkerEntitlementRole,
                com.force5solutions.care.cc.WorkerProfileArchive,
                com.force5solutions.care.cc.WorkerCertificationArchive,
                com.force5solutions.care.feed.HrInfo
        ])
        readOnly(true)
        driverClassName(ConfigurationHolder.config.top.db.driverClassName)
        url(ConfigurationHolder.config.top.db.url)
        username(ConfigurationHolder.config.top.db.username)
        password(ConfigurationHolder.config.top.db.password)
        logSql(false)
        dialect(ConfigurationHolder.config.top.db.dialect)
        hibernate {
            cache {
                use_second_level_cache(false)
                use_query_cache(false)
            }
        }
    }

}
