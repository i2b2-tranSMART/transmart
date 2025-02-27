package org.transmartproject.db.ontology

class ModifierDimensionCoreDb {

    String code
    Long   level
    String name
    String nodeType
    String path
    String studyId

    // unused
//    String modifierBlob
//    Date updateDate
//    Date downloadDate
//    Date importDate
//    Long uploadId

    static mapping = {
        table 'i2b2demodata.modifier_dimension'
        id      name: 'path', generator: 'assigned'
        version false

        code     column: 'modifier_cd'
        level    column: 'modifier_level'
	name     column: 'name_char'
        nodeType column: 'modifier_node_type' // known values: {L, F}
        path     column: 'modifier_path'
        studyId  column: 'sourcesystem_cd'
    }

    static constraints = {
        code           nullable: true, maxSize: 50
        level          nullable: true
        name           nullable: true, maxSize: 2000
        nodeType       nullable: true, maxSize: 10
        path           maxSize:  700
        studyId        nullable: true, maxSize: 50

        // unused:
//        modifierBlob   nullable: true
//        updateDate     nullable: true
//        downloadDate   nullable: true
//        importDate     nullable: true
//        uploadId       nullable: true
    }
}
