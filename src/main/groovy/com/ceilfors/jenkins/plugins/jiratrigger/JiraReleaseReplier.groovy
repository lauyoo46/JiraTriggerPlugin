package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import hudson.model.AbstractProject

class JiraReleaseReplier implements JiraTriggerListener {

    @Override
    void buildScheduled(Issue issue, Collection<? extends AbstractProject> projects) {

    }

    @Override
    void buildNotScheduled(Issue issue) {

    }
}
