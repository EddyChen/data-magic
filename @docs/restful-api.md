# RESTful API Documentation

Base URL: `http://localhost:8080`

## Health Check

### GET /api/health
Check if the API is running.

**Response:**
```json
{
  "status": "UP",
  "message": "Data Magic API is running"
}
```

---

## Processing Records

### GET /api/processing-records
Get all processing records with pagination.

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | int | 0 | Page number (0-indexed) |
| size | int | 10 | Page size |

**Response:** `PageResult<ProcessingRecordDTO>`

### GET /api/processing-records/{id}
Get processing record by ID.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Processing record ID |

**Response:** `ProcessingRecordDTO`

### GET /api/processing-records/status/{status}
Get processing records by status.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| status | string | Status: pending, processing, completed, failed, uploaded |

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | int | 0 | Page number |
| size | int | 10 | Page size |

**Response:** `PageResult<ProcessingRecordDTO>`

---

## Transformation Rules

### GET /api/transformation-rules
Get all transformation rules.

**Response:** `Array<TransformationRuleDTO>`

### GET /api/transformation-rules/active
Get all active transformation rules (ordered by priority).

**Response:** `Array<TransformationRuleDTO>`

### GET /api/transformation-rules/{id}
Get transformation rule by ID.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Rule ID |

**Response:** `TransformationRuleDTO`

### POST /api/transformation-rules
Create a new transformation rule.

**Request Body:** `TransformationRuleDTO`
```json
{
  "ruleCode": "MASK1",
  "rulePattern": ".*_MASK1$",
  "transformationType": "remove_suffix",
  "transformationParams": "_MASK1",
  "isActive": true,
  "priority": 1
}
```

**Response:** `TransformationRuleDTO`

### PUT /api/transformation-rules/{id}
Update a transformation rule.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Rule ID |

**Request Body:** `TransformationRuleDTO`

**Response:** `TransformationRuleDTO`

### DELETE /api/transformation-rules/{id}
Delete a transformation rule.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Rule ID |

**Response:** 200 OK

---

## System Configs

### GET /api/system-configs
Get all system configurations.

**Response:** `Array<SystemConfigDTO>`

### GET /api/system-configs/{id}
Get system config by ID.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Config ID |

**Response:** `SystemConfigDTO`

### GET /api/system-configs/key/{key}
Get system config by key.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| key | string | Config key |

**Response:** `SystemConfigDTO`

### POST /api/system-configs
Create a new system config.

**Request Body:** `SystemConfigDTO`
```json
{
  "configKey": "scan_directory",
  "configValue": "/data/input",
  "description": "Directory to scan for files"
}
```

**Response:** `SystemConfigDTO`

### PUT /api/system-configs/{id}
Update a system config.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Config ID |

**Request Body:** `SystemConfigDTO`

**Response:** `SystemConfigDTO`

### DELETE /api/system-configs/{id}
Delete a system config.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| id | long | Config ID |

**Response:** 200 OK

---

## Transformation Logs

### GET /api/transformation-logs/processing-record/{processingRecordId}
Get transformation logs by processing record ID.

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| processingRecordId | long | Processing record ID |

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | int | 0 | Page number |
| size | int | 20 | Page size |

**Response:** `PageResult<FieldTransformationLogDTO>`

---

## File Scan

### POST /api/scan/trigger
Manually trigger file scanning and processing.

**Response:**
```json
{
  "status": "success",
  "message": "File scan triggered"
}
```

---

## Data Models

### ProcessingRecordDTO
```json
{
  "id": 1,
  "sourceFileName": "data_SJBX_20260307.txt",
  "sourceFilePath": "/data/input/data_SJBX_20260307.txt",
  "targetFileName": "data_BJ_20260307_123456.txt",
  "targetFilePath": "/data/input/data_BJ_20260307_123456.txt",
  "totalRecordCount": 1000,
  "transformedFieldCount": 500,
  "status": "completed",
  "errorMessage": null,
  "startTime": "2026-03-07T12:00:00",
  "completeTime": "2026-03-07T12:01:30",
  "ftpUploaded": true,
  "ftpUploadTime": "2026-03-07T12:01:31",
  "sourceDeleted": true,
  "sourceDeleteTime": "2026-03-07T12:01:32",
  "createdAt": "2026-03-07T12:00:00"
}
```

### TransformationRuleDTO
```json
{
  "id": 1,
  "ruleCode": "MASK1",
  "rulePattern": ".*_MASK1$",
  "transformationType": "remove_suffix",
  "transformationParams": "_MASK1",
  "isActive": true,
  "priority": 1,
  "createdAt": "2026-03-07T10:00:00",
  "updatedAt": "2026-03-07T10:00:00"
}
```

### SystemConfigDTO
```json
{
  "id": 1,
  "configKey": "scan_directory",
  "configValue": "/data/input",
  "description": "Directory to scan for files",
  "createdAt": "2026-03-07T10:00:00",
  "updatedAt": "2026-03-07T10:00:00"
}
```

### FieldTransformationLogDTO
```json
{
  "id": 1,
  "processingRecordId": 1,
  "lineNumber": 10,
  "fieldIndex": 3,
  "fieldName": "phone",
  "originalValue": "13800138000_MASK1",
  "transformedValue": "13800138000",
  "ruleId": 1,
  "ruleCode": "MASK1",
  "createdAt": "2026-03-07T12:00:05"
}
```

### PageResult<T>
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

---

## Configuration Keys

Required system configurations:

| Key | Description |
|-----|-------------|
| scan_directory | Directory to scan for files with `_SJBX_` pattern |
| ftp_host | FTP server hostname |
| ftp_port | FTP server port (default: 21) |
| ftp_username | FTP username |
| ftp_password | FTP password |
| ftp_remote_path | FTP remote directory for uploads |

---

## Transformation Types

| Type | Description | Parameters |
|------|-------------|------------|
| remove_suffix | Remove suffix from field value | Suffix to remove (e.g., `_MASK1`) |
| replace | Replace substring | Comma-separated old,new values |
| hash | Hash the field value | - |
| mask | Mask the field value | Number of visible chars from start |