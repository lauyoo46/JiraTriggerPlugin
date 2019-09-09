package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.Version
import hudson.Extension
import hudson.model.Cause
import org.kohsuke.stapler.DataBoundConstructor

class JiraReleaseTrigger extends JiraTrigger<Version> {

    @SuppressWarnings('UnnecessaryConstructor')
    @DataBoundConstructor
    JiraReleaseTrigger() {
    }

    @Override
    boolean filter(Issue issue, Version version) {
        return true
    }

    @Override
    Cause getCause(Issue issue, Version version) {
        new JiraReleaseTriggerCause()
    }

    //todo: description
    @SuppressWarnings('UnnecessaryQualifiedReference')
    @Extension
    static class JiraVersionTriggerDescriptor extends JiraTrigger.JiraTriggerDescriptor {

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
