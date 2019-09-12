package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.Project
import com.atlassian.jira.rest.client.api.domain.Version
import hudson.Extension
import hudson.model.Cause
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class JiraReleaseTrigger extends JiraTrigger<Version> {

    public static final String DEFAULT_PROJECT_KEY = 'Project Key'

    @DataBoundSetter
    String projectKey = JiraReleaseTriggerDescriptor.DEFAULT_PROJECT_KEY_PATTERN

    @SuppressWarnings('UnnecessaryConstructor')
    @DataBoundConstructor
    JiraReleaseTrigger() {
    }

    @Override
    boolean filter(Issue issue, Version version) {
    }

    @Override
    boolean filter(Project project) {
        if(project.key == projectKey) {
            return true
        }
        return false
    }

    @Override
    Cause getCause(Issue issue, Version version) {
    }

    @Override
    Cause getCause(Project project) {
        new JiraReleaseTriggerCause()
    }

    @SuppressWarnings('UnnecessaryQualifiedReference')
    @Extension
    static class JiraReleaseTriggerDescriptor extends JiraTrigger.JiraTriggerDescriptor {

        @SuppressWarnings('GroovyUnusedDeclaration')
        public static final String DEFAULT_PROJECT_KEY_PATTERN = "(?i)${DEFAULT_PROJECT_KEY}"

        @Override
        String getDisplayName() {
            'Build when a new version is released'
        }
    }

    static class JiraReleaseTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            'JIRA version is released'
        }
    }

}
