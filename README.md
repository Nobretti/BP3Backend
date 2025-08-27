# BP3 Process Diagram Reducer

This Spring Boot application provides an API to reduce process diagrams by removing non-human steps and keeping only human tasks, start, and end nodes.

## Problem Description

The application processes BPMN-style process diagrams and reduces them to show only the human-interaction steps. Service tasks (automated steps) are removed while maintaining the logical flow between human tasks, start, and end nodes.

## API Endpoints

### 1. Reduce Diagram from JSON Body
**POST** `/api/diagramprocess/reduce`

Reduces a process diagram provided in the request body.

**Request Body:**
```json
{
  "nodes": [
    {
      "id": "0",
      "name": "Start",
      "type": "Start"
    },
    {
      "id": "1",
      "name": "A",
      "type": "ServiceTask"
    },
    {
      "id": "2",
      "name": "B",
      "type": "HumanTask"
    },
    {
      "id": "3",
      "name": "C",
      "type": "ServiceTask"
    },
    {
      "id": "4",
      "name": "D",
      "type": "HumanTask"
    },
    {
      "id": "5",
      "name": "End",
      "type": "End"
    }
  ],
  "edges": [
    {
      "from": "0",
      "to": "1"
    },
    {
      "from": "1",
      "to": "2"
    },
    {
      "from": "2",
      "to": "3"
    },
    {
      "from": "3",
      "to": "4"
    },
    {
      "from": "4",
      "to": "5"
    }
  ]
}
```

**Response:**
```json
{
  "nodes": [
    {
      "id": "0",
      "name": "Start",
      "type": "Start"
    },
    {
      "id": "2",
      "name": "B",
      "type": "HumanTask"
    },
    {
      "id": "4",
      "name": "D",
      "type": "HumanTask"
    },
    {
      "id": "5",
      "name": "End",
      "type": "End"
    }
  ],
  "edges": [
    {
      "from": "0",
      "to": "2"
    },
    {
      "from": "2",
      "to": "4"
    },
    {
      "from": "4",
      "to": "5"
    }
  ]
}
```

### 2. Reduce Diagram from File Upload
**POST** `/api/diagramprocess/reduce/file`

Reduces a process diagram from an uploaded JSON file.

**Request:** Multipart form data with a file parameter named "file" containing the JSON diagram.

## Node Types

- **Start**: The starting point of the process
- **End**: The ending point of the process  
- **HumanTask**: Tasks that require human interaction
- **ServiceTask**: Automated tasks (will be removed in reduction)
- **Gateway**: Decision points (will be removed in reduction)

## Algorithm

The reduction algorithm:

1. Identifies start and end nodes
2. Finds all human task nodes
3. Determines which human tasks are reachable from the start
4. Finds the optimal path through human tasks to reach the end
5. Creates direct connections between human tasks in the reduced diagram
6. Connects start to the first human task and the last human task to end

## Running the Application

### Prerequisites
- Java 21
- Gradle

### Build and Run
```bash
./gradlew build
./gradlew bootRun
```

The application will start on port 8080.

### Running Tests
```bash
./gradlew test
```

## Example Usage

1. **Using curl with JSON body:**
```bash
curl -X POST http://localhost:8080/api/diagramprocess/reduce \
  -H "Content-Type: application/json" \
  -d @sample-diagram.json
```

2. **Using curl with file upload:**
```bash
curl -X POST http://localhost:8080/api/diagramprocess/reduce/file \
  -F "file=@sample-diagram.json"
```

## Error Handling

The API returns appropriate HTTP status codes:
- **200 OK**: Successful reduction
- **400 Bad Request**: Invalid input (missing start/end nodes, malformed JSON)
- **500 Internal Server Error**: Unexpected server errors

## Sample Files

- `sample-diagram.json`: Example input diagram matching the problem description
