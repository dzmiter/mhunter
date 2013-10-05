package ru.dzmiter.controllers

/**
 * @author dzmiter
 */
class ViewStatsController {

    def hunterService

    def index() {
        render view: '/viewStats/index', model: [reports: hunterService.reports]
    }
}
