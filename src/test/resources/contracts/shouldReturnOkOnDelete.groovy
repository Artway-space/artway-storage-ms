import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Delete file by file id")
    request {
        method DELETE()
        url("/delete"){
            queryParameters {
                parameter 'fileId' : value(regex(nonBlank()))
            }
        }
    }
    response {
        status(200)
    }
}