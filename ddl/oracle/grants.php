<?php

$spec = [
    'BIOMART_USER' => [
        ['DEAPP',        '*TABLE',                    'READ', ''],
        ['DEAPP',        '*VIEW',                     'READ', ''],
        ['BIOMART',      '*TABLE',                    'WRITE', ''],
        ['BIOMART',      '*VIEW',                     'READ', ''],
        ['BIOMART',      '*SEQUENCE',                 'READ', ''],
        ['I2B2DEMODATA', '*TABLE',                    'READ', ''],
        ['I2B2DEMODATA', '*VIEW',                     'READ', ''],
        ['I2B2METADATA', '*TABLE',                    'READ', ''],
        ['I2B2METADATA', '*VIEW',                     'READ', ''],
        ['SEARCHAPP',    '*TABLE',                    'WRITE', ''],
        ['SEARCHAPP',    '*VIEW',                     'READ', ''],
        ['SEARCHAPP',    '*SEQUENCE',                 'READ', ''],
        ['FMAPP',        '*TABLE',                    'WRITE', ''],
        ['FMAPP',        '*SEQUENCE',                 'READ', ''],
        ['AMAPP',        '*TABLE',                    'WRITE', ''],
        ['AMAPP',        '*SEQUENCE',                 'READ', ''],
        ['AMAPP',        '*VIEW',                     'READ', ''],
        ['I2B2DEMODATA', 'QT_QUERY_MASTER',           'WRITE', ''],
        ['I2B2DEMODATA', 'QT_QUERY_INSTANCE',         'WRITE', ''],
        ['I2B2DEMODATA', 'QT_QUERY_RESULT_INSTANCE',  'WRITE', ''],
        ['I2B2DEMODATA', 'QT_PATIENT_SET_COLLECTION', 'WRITE', ''],
        ['I2B2DEMODATA', 'ASYNC_JOB',                 'WRITE', ''],
        ['I2B2DEMODATA', '*SEQUENCE',                 'READ', ''],
        ['DEAPP',        'DE_SAVED_COMPARISON',       'WRITE', ''],
        ['BIOMART',      'BIO_ASSAY_FEATURE_GROUP',   'WRITE', ''],
        ['BIOMART',      'BIO_MARKER',                'WRITE', ''],
        ['BIOMART',      'BIO_ASSAY_DATA_ANNOTATION', 'WRITE', ''],
        ['GALAXY',       '*TABLE',                    'WRITE', ''],
        ['GALAXY',       '*SEQUENCE',                 'READ', ''],
    ],
    'TM_CZ' => [
        ['DEAPP',        '*TABLE',             'FULL', ''],
        ['DEAPP',        '*SEQUENCE',          'READ', ''],
        ['BIOMART',      '*TABLE',             'FULL', ''],
        ['BIOMART',      '*VIEW',              'READ', ''],
        ['BIOMART',      'TEA_NPV_PRECOMPUTE', 'EXECUTE', ''], /* I2B2_LOAD_OMICSOFT_DATA */
        ['BIOMART',      '*SEQUENCE',          'READ', ''],
        ['I2B2DEMODATA', '*TABLE',             'FULL', ''],
        ['I2B2DEMODATA', '*SEQUENCE',          'READ', ''],
        ['I2B2DEMODATA', '*VIEW',              'READ', ''],
        ['I2B2METADATA', '*TABLE',             'FULL', ''],
        ['I2B2METADATA', '*SEQUENCE',          'READ', ''],
        ['SEARCHAPP',    '*TABLE',             'FULL', ''],
        ['SEARCHAPP',    '*VIEW',              'READ', ''],
        ['SEARCHAPP',    '*SEQUENCE',          'READ', ''],
        ['TM_LZ',        '*TABLE',             'FULL', ''],
        ['TM_LZ',        '*VIEW',              'READ', ''],
        ['TM_WZ',        '*TABLE',             'FULL', ''],
        ['TM_WZ',        '*VIEW',              'READ', ''],
        ['FMAPP',        '*TABLE',             'FULL', ''],
        ['AMAPP',        '*TABLE',             'FULL', ''],
        ['GALAXY',       '*TABLE',             'FULL', ''],
        ['GALAXY',       '*SEQUENCE',          'READ', ''],
    ],
    'SEARCHAPP' => [
        ['BIOMART', 'BIO_ASSAY_DATA_ANNOTATION', 'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_MARKER_CORREL_MV',      'READ', 'GRANT_OPTION']
    ],
    'BIOMART' => [
        ['DEAPP', 'DE_METABOLITE_SUPER_PATHWAYS', 'READ', 'GRANT_OPTION'],
        ['DEAPP', 'DE_METABOLITE_SUB_PATHWAYS',   'READ', 'GRANT_OPTION'],
        ['DEAPP', 'DE_METABOLITE_SUB_PWAY_METAB', 'READ', 'GRANT_OPTION'],
        ['DEAPP', 'DE_METABOLITE_ANNOTATION',     'READ', 'GRANT_OPTION'],
        ['DEAPP', 'DE_MRNA_ANNOTATION',           'READ', 'GRANT_OPTION'],
    ],
    'AMAPP' => [
        ['BIOMART', 'BIO_DATA_UID',       'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_CONCEPT_CODE',   'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_DISEASE',        'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_ASSAY_PLATFORM', 'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_COMPOUND',       'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_MARKER',         'READ', 'GRANT_OPTION'],
        ['BIOMART', 'BIO_OBSERVATION',    'READ', 'GRANT_OPTION'],
    ],
];

// vim: et ts=4:
