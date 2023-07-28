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
  - name: SENTRY_ENV
    value: {{ .Values.java.host_env }}
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_REGISTRATION_ID
    value: {{ .Values.maatApi.registrationId }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: MAAT_API_OAUTH_CLIENT_ID
    value: {{ .Values.maatApi.clientId }}
  - name: MAAT_API_OAUTH_CLIENT_SECRET
    value: {{ .Values.maatApi.clientSecret }}
  - name: MAAT_API_OAUTH_SCOPE
    value: {{ .Values.maatApi.oauthScope }}
  - name: NOTIFY_KEY
    value: {{ .Values.emailClient.notify_key }}
{{- end -}}