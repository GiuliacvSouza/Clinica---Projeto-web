ALTER TABLE IF EXISTS fatura
    ADD COLUMN IF NOT EXISTS valor_base numeric(10,2)
@@

ALTER TABLE IF EXISTS fatura
    ADD COLUMN IF NOT EXISTS taxa_iva numeric(5,2)
@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'fatura'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'fatura'
              AND column_name = 'valor_final'
        ) THEN
            EXECUTE 'UPDATE fatura SET valor_base = COALESCE(valor_base, valor_final, 0)';
        ELSE
            EXECUTE 'UPDATE fatura SET valor_base = COALESCE(valor_base, 0)';
        END IF;
    END IF;
END
$$
@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'fatura'
    ) THEN
        UPDATE fatura
        SET taxa_iva = COALESCE(taxa_iva, 0);
    END IF;
END
$$
@@

ALTER TABLE IF EXISTS fatura
    ALTER COLUMN valor_base SET NOT NULL
@@

ALTER TABLE IF EXISTS fatura
    ALTER COLUMN taxa_iva SET NOT NULL
@@

ALTER TABLE IF EXISTS fatura
    ALTER COLUMN taxa_iva SET DEFAULT 0
@@

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'fatura'
    ) THEN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = current_schema()
              AND table_name = 'fatura'
              AND column_name = 'valor_final'
              AND is_generated <> 'NEVER'
        ) THEN
            ALTER TABLE IF EXISTS fatura
                DROP COLUMN IF EXISTS valor_final;

            ALTER TABLE IF EXISTS fatura
                ADD COLUMN valor_final numeric(10,2)
                GENERATED ALWAYS AS (
                    ROUND((COALESCE(valor_base, 0) * (1 + COALESCE(taxa_iva, 0) / 100.0))::numeric, 2)
                ) STORED;
        END IF;
    END IF;
END
$$
@@

ALTER TABLE IF EXISTS consulta
    DROP CONSTRAINT IF EXISTS consulta_status_check
@@

ALTER TABLE IF EXISTS consulta
    ADD CONSTRAINT consulta_status_check
    CHECK (status IN (
        'AGENDADA',
        'CONFIRMADA',
        'EM_ATENDIMENTO',
        'CONCLUIDA',
        'FATURADA',
        'CANCELADA',
        'FALTA',
        'PENDENTE',
        'EM_ESPERA',
        'EM_CONSULTA'
    ))
@@

-- Códigos postais de teste (inserção segura: ignora se já existir)
INSERT INTO codigo_postal (codigo_postal, localidade) VALUES
    ('4900-001', 'Viana do Castelo'),
    ('4900-360', 'Viana do Castelo'),
    ('4700-000', 'Braga'),
    ('4700-307', 'Braga'),
    ('4000-001', 'Porto'),
    ('4000-285', 'Porto'),
    ('1000-001', 'Lisboa'),
    ('1000-205', 'Lisboa'),
    ('3000-001', 'Coimbra'),
    ('4480-000', 'Vila do Conde'),
    ('4460-000', 'Matosinhos'),
    ('4410-000', 'Vila Nova de Gaia'),
    ('4200-001', 'Porto'),
    ('2750-001', 'Cascais'),
    ('8000-001', 'Faro')
ON CONFLICT (codigo_postal) DO NOTHING
@@
