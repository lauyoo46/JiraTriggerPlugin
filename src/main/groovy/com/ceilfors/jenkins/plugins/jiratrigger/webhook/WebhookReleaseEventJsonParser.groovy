package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import com.atlassian.jira.rest.client.internal.json.ProjectJsonParser
import com.atlassian.jira.rest.client.internal.json.VersionJsonParser
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import jenkins.model.Jenkins
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject


class WebhookReleaseEventJsonParser implements JsonObjectParser<WebhookReleaseEvent> {

    private final ProjectJsonParser projectJsonParser = new ProjectJsonParser()

    @Override
    WebhookReleaseEvent parse(JSONObject webhookEvent) throws JSONException {

        JiraTriggerGlobalConfiguration jtgc = Jenkins.instance.getDescriptor(JiraTriggerGlobalConfiguration) as JiraTriggerGlobalConfiguration
        String authString = jtgc.getEncodedAuthentication()
        String projectId = webhookEvent.getJSONObject('version').getString('projectId')
        def addr = "http://10.106.0.77:8081/rest/api/2/project/"
        addr += projectId
        def conn = addr.toURL().openConnection() as HttpURLConnection
        conn.setRequestProperty("Authorization", "Basic ${authString}")
        conn.requestMethod = 'GET'
        JSONObject jsonResponse = new JSONObject(conn.content.text)

        new WebhookReleaseEvent(
                webhookEvent.getLong('timestamp'),
                webhookEvent.getString('webhookEvent'),
                projectJsonParser.parse(jsonResponse),
                new VersionJsonParser().parse(webhookEvent.getJSONObject('version'))
        )
    }
}
