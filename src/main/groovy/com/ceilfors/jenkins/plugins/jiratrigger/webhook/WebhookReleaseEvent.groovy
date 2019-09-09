package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.api.domain.Project
import com.atlassian.jira.rest.client.api.domain.Version

class WebhookReleaseEvent extends BaseWebhookEvent {

    final Version version
    final Project project

    WebhookReleaseEvent(long timestamp, String webhookEventType, Project project, Version version) {
        super(timestamp, webhookEventType)
        this.project = project
        this.version = version
    }

}
