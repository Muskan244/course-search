# Course Search Application

A Spring Boot application that provides a REST API for searching courses with multiple filters, pagination, and sorting capabilities. The application uses Elasticsearch for efficient full-text search and filtering.

## Features

- **Full-text search** on course titles and descriptions
- **Multiple filters**: category, type, age range, price range, and date
- **Sorting options**: by date (upcoming), price (ascending/descending)
- **Pagination support**
- **Elasticsearch integration** for fast and efficient search
- **Docker Compose setup** for easy Elasticsearch deployment
- **🎯 BONUS: Autocomplete suggestions** for course titles
- **🎯 BONUS: Fuzzy search** with typo tolerance

## Prerequisites

- Java 21
- Docker and Docker Compose
- Gradle (or use the included Gradle wrapper)

## Quick Start

### 1. Start Elasticsearch

```bash
docker-compose up -d
```

Verify Elasticsearch is running:

```bash
curl http://localhost:9200
```

### 2. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080` and automatically:

- Connect to Elasticsearch
- Create the courses index with proper mappings
- Load sample data from `src/main/resources/sample-courses.json`

### 3. Test the API

```bash
# Get all courses
curl "http://localhost:8080/api/search"

# Search for math courses
curl "http://localhost:8080/api/search?q=math"

# Filter by category and sort by price
curl "http://localhost:8080/api/search?category=Math&sort=priceDesc"

# Search with multiple filters
curl "http://localhost:8080/api/search?q=math&minAge=6&sort=priceAsc&page=0&size=5"

# 🎯 BONUS: Autocomplete suggestions
curl "http://localhost:8080/api/search/suggest?q=phy"

# 🎯 BONUS: Fuzzy search with typos
curl "http://localhost:8080/api/search?q=geometri"
```

## API Documentation

### Endpoint: `GET /api/search`

Search for courses with various filters and sorting options.

#### Query Parameters

| Parameter   | Type    | Required | Default    | Description                                                   |
| ----------- | ------- | -------- | ---------- | ------------------------------------------------------------- |
| `q`         | String  | No       | -          | Search query for title and description (with fuzzy matching)  |
| `category`  | String  | No       | -          | Filter by category (e.g., "Math", "Science")                  |
| `type`      | String  | No       | -          | Filter by type ("ONE_TIME", "COURSE", "CLUB")                 |
| `minAge`    | Integer | No       | -          | Minimum age filter                                            |
| `maxAge`    | Integer | No       | -          | Maximum age filter                                            |
| `minPrice`  | Double  | No       | -          | Minimum price filter                                          |
| `maxPrice`  | Double  | No       | -          | Maximum price filter                                          |
| `startDate` | Date    | No       | -          | Filter courses on or after this date (ISO format: YYYY-MM-DD) |
| `sort`      | String  | No       | "upcoming" | Sort order: "upcoming", "priceAsc", "priceDesc"               |
| `page`      | Integer | No       | 0          | Page number (0-based)                                         |
| `size`      | Integer | No       | 10         | Number of results per page                                    |

### 🎯 BONUS Endpoint: `GET /api/search/suggest`

Get autocomplete suggestions for course titles.

#### Query Parameters

| Parameter | Type   | Required | Description                          |
| --------- | ------ | -------- | ------------------------------------ |
| `q`       | String | Yes      | Partial title to get suggestions for |

#### Response Format

```json
["Physics Basics", "Creative Writing", "Math Fun 101"]
```

#### Response Format

```json
{
  "total": 50,
  "courses": [
    {
      "id": "1",
      "title": "Geometry Genius",
      "description": "This is a detailed description of the course titled 'Geometry Genius'.",
      "category": "Math",
      "type": "ONE_TIME",
      "gradeRange": "9th–12th",
      "minAge": 8,
      "maxAge": 10,
      "price": 386.73,
      "nextSessionDate": "2025-08-25"
    }
  ]
}
```

## Example API Calls

### Basic Search

```bash
# Search for courses containing "math"
curl "http://localhost:8080/api/search?q=math"
```

### Filter by Category

```bash
# Get all Math courses
curl "http://localhost:8080/api/search?category=Math"
```

### Price Range Filter

```bash
# Get courses between $50 and $200
curl "http://localhost:8080/api/search?minPrice=50&maxPrice=200"
```

### Age Range Filter

```bash
# Get courses for ages 8-12
curl "http://localhost:8080/api/search?minAge=8&maxAge=12"
```

### Date Filter

```bash
# Get courses starting from August 1st, 2025
curl "http://localhost:8080/api/search?startDate=2025-08-01"
```

### Sorting Examples

```bash
# Sort by price (lowest first)
curl "http://localhost:8080/api/search?sort=priceAsc"

# Sort by price (highest first)
curl "http://localhost:8080/api/search?sort=priceDesc"

# Sort by upcoming dates (default)
curl "http://localhost:8080/api/search?sort=upcoming"
```

### Combined Filters

```bash
# Search for math courses for ages 6-10, sorted by price ascending
curl "http://localhost:8080/api/search?q=math&minAge=6&maxAge=10&sort=priceAsc"

# Get Science courses starting from August 1st, sorted by price descending
curl "http://localhost:8080/api/search?category=Science&startDate=2025-08-01&sort=priceDesc"
```

### Pagination

```bash
# Get first 5 results
curl "http://localhost:8080/api/search?page=0&size=5"

# Get next 5 results
curl "http://localhost:8080/api/search?page=1&size=5"
```

### 🎯 BONUS: Autocomplete Examples

```bash
# Get suggestions for "phy"
curl "http://localhost:8080/api/search/suggest?q=phy"
# Returns: ["Physics Basics"]

# Get suggestions for "math"
curl "http://localhost:8080/api/search/suggest?q=math"
# Returns: ["Math Fun 101"]

# Get suggestions for "cre"
curl "http://localhost:8080/api/search/suggest?q=cre"
# Returns: ["Creative Writing"]
```

### 🎯 BONUS: Fuzzy Search Examples

```bash
# Search with typo - "geometri" should match "Geometry Genius"
curl "http://localhost:8080/api/search?q=geometri"
# Returns: Courses with "Geometry Genius" in the title

# Search with partial word - "dinors" should match "Dinosaurs 101"
curl "http://localhost:8080/api/search?q=dinors"
# Returns: Courses with "Dinosaurs" in the title
```

## Project Structure

```
course-search/
├── src/main/java/com/example/course_search/
│   ├── CourseSearchApplication.java          # Main application class
│   ├── controller/
│   │   └── CourseSearchController.java      # REST API controller
│   ├── document/
│   │   └── CourseDocument.java             # Elasticsearch document model
│   ├── repository/
│   │   └── CourseRepository.java           # Spring Data repository
│   └── service/
│       ├── CourseLoader.java               # Data loading service
│       ├── CourseSearchService.java        # Search business logic
│       └── AutocompleteService.java        # 🎯 BONUS: Autocomplete service
├── src/main/resources/
│   ├── application.properties              # Application configuration
│   └── sample-courses.json                # Sample course data
├── docker-compose.yml                      # Elasticsearch setup
├── build.gradle                           # Gradle build configuration
└── README.md                              # This file
```

## Data Model

Each course document contains:

- `id`: Unique identifier
- `title`: Course title (searchable with fuzzy matching)
- `description`: Course description (searchable)
- `category`: Course category (exact match filter)
- `type`: Course type - "ONE_TIME", "COURSE", or "CLUB" (exact match filter)
- `gradeRange`: Target grade range
- `minAge`/`maxAge`: Age range (numeric filters)
- `price`: Course price (numeric filter)
- `nextSessionDate`: Next session date (date filter)
- `titleSuggest`: 🎯 BONUS: Field for autocomplete suggestions

## Elasticsearch Configuration

The application connects to Elasticsearch on `localhost:9200` and creates an index called `courses` with the following mappings:

- **Text fields** (`title`, `description`): Full-text search with standard analyzer
- **Keyword fields** (`category`, `type`): Exact match filtering
- **Numeric fields** (`minAge`, `maxAge`, `price`): Range queries
- **Date field** (`nextSessionDate`): Date range queries
- **Completion field** (`titleSuggest`): 🎯 BONUS: Autocomplete suggestions

## 🎯 BONUS Features

### Autocomplete Suggestions

- **Endpoint**: `GET /api/search/suggest?q={partialTitle}`
- **Functionality**: Returns up to 10 course titles that start with the provided partial title
- **Implementation**: Uses prefix search on the `titleSuggest` field
- **Example**: `q=phy` returns `["Physics Basics"]`

### Fuzzy Search Enhancement

- **Functionality**: The main search endpoint now supports fuzzy matching on course titles
- **Implementation**: Uses Elasticsearch's fuzzy matching with typo tolerance
- **Example**: Searching for `geometri` will match `Geometry Genius`
- **Benefit**: Users can find courses even with typos or partial spellings

## Development

### Running the Project

```bash
./gradlew bootRun
```

### Stopping the Application

```bash
# Stop the Spring Boot application
pkill -f "gradle.*bootRun"

# Stop Elasticsearch
docker-compose down
```

## Troubleshooting

### Elasticsearch Connection Issues

- Ensure Docker is running
- Check if Elasticsearch is accessible: `curl http://localhost:9200`
- Restart Elasticsearch: `docker-compose restart`

### Data Loading Issues

- Check application logs for data loading errors
- Verify `sample-courses.json` is in the correct location
- Restart the application to reload data

### Search Issues

- Verify the courses index exists: `curl "http://localhost:9200/courses/_search"`
- Check if data is loaded: `curl "http://localhost:8080/api/search"`

### 🎯 BONUS Feature Issues

- Test autocomplete: `curl "http://localhost:8080/api/search/suggest?q=phy"`
- Test fuzzy search: `curl "http://localhost:8080/api/search?q=geometri"`

## Sample Data

The application comes with 50 sample courses covering various categories:

- Math, Science, Art, History, Coding
- Different age ranges (5-16 years)
- Price range: ₹14.55 - ₹475.56
- Session dates spanning July-August 2025

This provides a good dataset for testing all search and filter functionality, including the bonus autocomplete and fuzzy search features.
