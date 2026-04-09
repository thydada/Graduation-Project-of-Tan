erDiagram
    USER ||--o{ PACKAGE : "1:N"
    USER ||--o{ MESSAGE : "1:N"
    USER ||--o{ EXCEPTION_PACKAGE : "1:N"
    EMPLOYEE ||--o{ PACKAGE_ENTRY : "1:N"
    EMPLOYEE ||--o{ PACKAGE_OUTBOUND : "1:N"
    EMPLOYEE ||--o{ EXCEPTION_PACKAGE : "1:N"
    ADMIN ||--o{ MESSAGE : "1:N"
    PACKAGE ||--o| SHELF : "1:1"
    PACKAGE ||--o{ PACKAGE_ENTRY : "1:N"
    PACKAGE ||--o{ PACKAGE_OUTBOUND : "1:N"
    PACKAGE ||--o{ EXCEPTION_PACKAGE : "1:N"

    USER {
        long id PK
        string username UK
        string password
        string realName
        string phone
        string email
        string idCard
        tinyint status
        datetime createTime
        datetime updateTime
    }

    EMPLOYEE {
        long id PK
        string username UK
        string password
        string realName
        string phone
        string email
        string department
        string position
        tinyint status
        datetime createTime
        datetime updateTime
    }

    ADMIN {
        long id PK
        string username UK
        string password
        string realName
        string phone
        string email
        tinyint status
        datetime createTime
        datetime updateTime
    }

    PACKAGE {
        long id PK
        string trackingNumber UK
        string senderName
        string senderPhone
        string senderAddress
        string receiverName
        string receiverPhone
        string receiverAddress
        string packageType
        decimal weight
        string size
        string status
        long warehouseId
        long shelfId
        integer shelfLayer
        string pickupCode
        long entryEmployeeId
        datetime entryTime
        long userId FK
        datetime pickupDeadline
        long deliveryEmployeeId
        datetime createTime
        datetime updateTime
    }

    PACKAGE_ENTRY {
        long id PK
        long packageId FK
        long employeeId FK
        long warehouseId
        long shelfId
        integer shelfLayer
        string entryMethod
        datetime entryTime
        string remarks
        datetime createTime
    }

    PACKAGE_OUTBOUND {
        long id PK
        long packageId FK
        long outboundEmployeeId FK
        long deliveryEmployeeId FK
        datetime outboundTime
        string remarks
        datetime createTime
    }

    EXCEPTION_PACKAGE {
        long id PK
        long packageId FK
        string trackingNumber
        string exceptionType
        string exceptionReason
        long reportEmployeeId FK
        string reportEmployeeName
        long handleEmployeeId
        string handleStatus
        string handleResult
        string source
        long userId FK
        datetime reportTime
        datetime handleTime
        datetime updateTime
    }

    SHELF {
        long id PK
        string shelfCode UK
        long warehouseId
        string shelfType
        integer capacity
        integer currentCount
        tinyint status
        datetime createTime
        datetime updateTime
    }

    MESSAGE {
        long id PK
        string title
        text content
        string messageType
        string senderType
        long senderId
        string receiverType
        long receiverId
        long warehouseId
        string status
        datetime sendTime
        datetime readTime
    }
