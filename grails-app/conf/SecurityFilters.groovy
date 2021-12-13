import com.force5solutions.care.common.Secured

class SecurityFilters {

    def permissionService
    def grailsApplication

    def filters = {

        validatePermission(controller: '*', action: '*') {
            before = {
                if (session.loggedUser && controllerName) {
                    def controllerClass = grailsApplication.controllerClasses.find {it.logicalPropertyName == controllerName}
                    def annotation = controllerClass.clazz.getAnnotation(Secured)
                    String currentAction = actionName ?: controllerClass.defaultActionName

                    if (!annotation || currentAction in annotation.exclude()) {
                        def action = applicationContext.getBean(controllerClass.fullName).class.declaredFields.find { field -> field.name == currentAction }
                        annotation = action ? action.getAnnotation(Secured) : null
                    }

                    boolean hasPermission = annotation ? permissionService.hasPermission(annotation.value()) : true

                    if (!hasPermission) {
                        render(view: "/permissionDenied")
                        return false;
                    }
                }
            }
        }
    }
}
