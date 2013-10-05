modules = {
    application {
        resource url:'js/application.js'
    }
    jquery {
        resource url:'js/jquery-2.0.3.min.js'
    }
    bootstrap {
        dependsOn 'jquery'

        resource url:'css/bootstrap.min.css', disposition: 'head'
        resource url:'css/bootstrap-responsive.min.css', disposition: 'head'
        resource url:'js/bootstrap.min.js'
    }
}