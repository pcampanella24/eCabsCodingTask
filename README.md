# Ride Matching Service 

**Java backend for ride matching:** assigns the nearest available driver to a rider's pickup location, with full thread-safety for concurrent requests. 

--- 

## **Overview** 

**Task requirements:** 
- Register/update drivers with location and availability
- Request ride: allocate nearest driver (Euclidean distance), mark as busy
- Complete ride: free the driver - Get "X" nearest available drivers from a location, sorted by distance
- Pure in-memory (no DB), focused on efficiency and concurrency

--- 

## **Tech Stack** 
- Java 21
- Maven
- JUnit 5 (tests covering logic, edge cases, and concurrency)
- No frameworks — lightweight design using:
  - ConcurrentHashMap
  - AtomicBoolean
  - ReadWriteLock

---

  ## **Project Structure**

  - model/ - Location, Driver, Ride
  - service/ - RideMatchingService (core)
  - exception/ - Custom exceptions
  - utils/ - Reusable methods
  - constants/ - Config values

---

## **Setup & Run** 

**Requirements:** 
- JDK 21
- Maven 3.6+

### Commands
```bash
git clone https://github.com/pcampanella24/eCabsCodingTask.git

mvn clean install

# Run demo scenarios
mvn exec:java

# Run all tests
mvn test
```

---

## **Key Implementation Choices** 

- **Distance:** Straight-line Euclidean √((x₂-x₁)² + (y₂-y₁)²)
- **Thread-Safety:**
  - ConcurrentHashMap for storage.
  - ReadWriteLock for safe reads during queries.
  - CAS (Compare And Set) for allocation:
    - This ensures:
      - Only one thread can allocate a driver
      - No race conditions
      - Lock-free and high performance -

- **Exceptions:**
  - Custom for clear handling.

  **I went with this approach because I like how it balances simplicity with rock-solid concurrency.**

---

## **Testing** 

**Comprehensive unit tests:** 
- Core logic: registration, matching, completion, nearest drivers.
- Edge cases: no drivers, all busy, invalid states.
- Concurrency: Multi-threaded scenarios verify no double-allocations or races.

---
  
## **Notes** 

In-memory only, data lost on restart.
