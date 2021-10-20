import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return FileId on File Save")
    request {
        method POST()
        url("/save")
        multipart(
                [
                        file: named(
                                name: value(stub(regex('.+')), test('file')),
                                content: value(consumer(regex(nonEmpty())))
                        )
                ]
        )

        headers {
            contentType('multipart/form-data')
        }
    }
    response {
        status(200)
        body([
            id: 'jdkjs-djskjd-dksjd-sjdk-sjs8sk',
            name: 'andrew'
        ])
        headers {
            contentType('application/json')
        }
    }
}