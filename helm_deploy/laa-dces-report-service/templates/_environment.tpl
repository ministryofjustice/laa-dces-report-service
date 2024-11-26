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
  - name: NOTIFY_KEY
    value: {{ .Values.emailClient.notify_key }}
  - name: NOTIFY_TEMPLATEID
    value: {{ .Values.emailClient.notify_templateId }}
{{- end -}}