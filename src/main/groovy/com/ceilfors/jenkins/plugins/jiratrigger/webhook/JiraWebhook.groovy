package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.UnprotectedRootAction
import org.codehaus.jettison.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.interceptor.RequirePOST

import javax.inject.Inject
import java.util.logging.Level
/**
 * The HTTP endpoint that receives JIRA Webhook.
 *
 * @author ceilfors
 */
@Log
@Extension
class JiraWebhook implements UnprotectedRootAction {

    public static final URL_NAME = 'jira-trigger-webhook-receiver'
    public static final ISSUE_UPDATED_WEBHOOK_EVENT = 'jira:issue_updated'
    public static final COMMENT_CREATED_WEBHOOK_EVENT = 'comment_created'
    public static final VERSION_RELEASED_WEBHOOK_EVENT = 'jira:version_released'
    private JiraWebhookListener jiraWebhookListener

    @Inject
    void setJiraWebhookListener(JiraWebhookListener jiraWebhookListener) {
        this.jiraWebhookListener = jiraWebhookListener
    }

    @Override
    String getIconFileName() {
        null
    }

    @Override
    String getDisplayName() {
        'JIRA Trigger'
    }

    @Override
    String getUrlName() {
        URL_NAME
    }

    @SuppressWarnings('GroovyUnusedDeclaration')
    @RequirePOST
    void doIndex(StaplerRequest request) {
        processEvent(request, getRequestBody(request))
    }

    void processEvent(StaplerRequest request, String webhookEvent) {
        logJson(webhookEvent)
        Map webhookEventMap = new JsonSlurper().parseText(webhookEvent) as Map
        RawWebhookEvent rawWebhookEvent = new RawWebhookEvent(request, webhookEventMap)
        JSONObject webhookJsonObject = new JSONObject(webhookEvent)
        boolean validEvent = false

        if (rawWebhookEvent.isChangelogEvent()) {
            log.fine("Received Webhook callback from changelog. Event type: ${rawWebhookEvent.eventType}")
            WebhookChangelogEvent changelogEvent = new WebhookChangelogEventJsonParser().parse(webhookJsonObject)
            changelogEvent.userId = rawWebhookEvent.userId
            changelogEvent.userKey = rawWebhookEvent.userKey
            jiraWebhookListener.changelogCreated(changelogEvent)
            validEvent = true
        }
        if (rawWebhookEvent.isCommentEvent()) {
            log.fine("Received Webhook callback from comment. Event type: ${rawWebhookEvent.eventType}")
            WebhookCommentEvent commentEvent = new WebhookCommentEventJsonParser().parse(webhookJsonObject)
            commentEvent.userId = rawWebhookEvent.userId
            commentEvent.userKey = rawWebhookEvent.userKey
            jiraWebhookListener.commentCreated(commentEvent)
            validEvent = true
        }
        if(rawWebhookEvent.isReleaseEvent()) {
            log.fine("Received Webhook callback from release. Event type: ${rawWebhookEvent.eventType}")
            WebhookReleaseEvent releaseEvent = new WebhookReleaseEventJsonParser().parse(webhookJsonObject)
            releaseEvent.userId = rawWebhookEvent.userId
            releaseEvent.userKey = rawWebhookEvent.userKey
            jiraWebhookListener.releaseCreated(releaseEvent)
            validEvent = true

        }
        if (!validEvent) {
            log.warning('Received Webhook callback with an invalid event type or a body without comment/changelog/version. ' +
                    "Event type: ${rawWebhookEvent.eventType}. Event body contains: ${webhookEventMap.keySet()}.")
        }
    }

    private void logJson(String webhookEvent) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest('Webhook event body:')
            log.finest(JsonOutput.prettyPrint(webhookEvent))
        }
    }

    private String getRequestBody(StaplerRequest req) {
        req.inputStream.text
    }

    private static class RawWebhookEvent {

        final StaplerRequest request
        final Map webhookEventMap

        RawWebhookEvent(StaplerRequest request, Map webhookEventMap) {
            this.request = request
            this.webhookEventMap = webhookEventMap
        }

        boolean isChangelogEvent() {
            eventType == ISSUE_UPDATED_WEBHOOK_EVENT && webhookEventMap['changelog']
        }

        boolean isCommentEvent() {
            (eventType == ISSUE_UPDATED_WEBHOOK_EVENT
                    || eventType == COMMENT_CREATED_WEBHOOK_EVENT) && webhookEventMap['comment']
        }

        boolean isReleaseEvent() {
            eventType == VERSION_RELEASED_WEBHOOK_EVENT && webhookEventMap['version']
        }

        String getUserId() {
            request.getParameter('user_id')
        }

        String getUserKey() {
            request.getParameter('user_key')
        }

        String getEventType() {
            webhookEventMap['webhookEvent']
        }
    }
}
