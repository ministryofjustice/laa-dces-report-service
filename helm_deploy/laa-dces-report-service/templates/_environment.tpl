{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for service containers
*/}}
{{- define "laa-dces-report-service.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry_dsn }}
  - name: HOST_ENV
    value: {{ .Values.java.host_env }}
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_REGISTRATION_ID
    value: {{ .Values.maatApi.registrationId }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: MAAT_API_OAUTH_CLIENT_ID
    valueFrom:
        secretKeyRef:
            name: maat-api-oauth-client-id
            key: MAAT_API_OAUTH_CLIENT_ID
  - name: MAAT_API_OAUTH_CLIENT_SECRET
    valueFrom:
        secretKeyRef:
            name: maat-api-oauth-client-secret
            key: MAAT_API_OAUTH_CLIENT_SECRET
  - name: MAAT_API_OAUTH_SCOPE
    value: {{ .Values.maatApi.oauthScope }}
  - name: NOTIFY_KEY
    value: {{ .Values.emailClient.notify_key }}
  - name: NOTIFY_TEMPLATEID
    value: {{ .Values.emailClient.notify_templateId }}
  - name: NOTIFY_RECIPIENT
    value: {{ .Values.emailClient.notify_recipient }}
  - name: DCES_CRON_ENABLED
    value: {{ .Values.scheduling.dces_cron_enabled | quote}}
  - name: DCES_CRON_CONTRIBUTIONS
    value: {{ .Values.scheduling.dces_cron_contributions }}
  - name: DCES_CRON_FDC
    value: {{ .Values.scheduling.dces_cron_fdc }}
{{- end -}}