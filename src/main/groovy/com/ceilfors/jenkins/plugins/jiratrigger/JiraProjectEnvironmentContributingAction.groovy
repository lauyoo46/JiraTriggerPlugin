package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Project
import hudson.model.EnvironmentContributingAction

class JiraProjectEnvironmentContributingAction implements EnvironmentContributingAction {

    String projectKey

    JiraProjectEnvironmentContributingAction(Project project) {
        this.projectKey = project.key
    }

    @Override
    String getIconFileName() {
        return null
    }

    @Override
    String getDisplayName() {
        return null
    }

    @Override
    String getUrlName() {
        return null
    }
}
