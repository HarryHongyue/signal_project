# Design Explanation Document

## Data Access Layer Design Principles

The data access layer is the foundation of the cardiovascular health monitoring system and is responsible for acquiring, parsing, and standardizing data from multiple sources. It insulates the rest of the system from the details of the data acquisition methods, thereby adapting to different clinical settings and evolving technologies.

**DataListener** serves as an abstract interface that defines how the system receives data (regardless of its source). This interface allows the system to use multiple input methods without changing the internal processing logic.

**TCPDataListener**, **WebSocketDataListener**, and **FileDataListener** implement specialized data reception for different protocols and data sources. Each handles the technical details of its own communication method while conforming to a common interface.

**DataParser** converts raw input data into objects that can be processed by the system. It supports multiple formats (CSV, JSON) to ensure consistent translation of data from any source.

**PatientData** encapsulates the parsed, standardized information as common information throughout the system. Its structured format enables consistent processing regardless of the original data source.

**DataSourceAdapter** connects external data sources to the internal storage system. It validates incoming data with a `PatientIdentifier` before passing it to `DataStorage`, ensuring that only valid, identified data enters the system.

**DataHandler** defines the callback interface for asynchronous data processing, allowing listeners to receive and forward data without blocking.
