-- Database: base_dev

BEGIN;

CREATE TABLE IF NOT EXISTS public.itens
(
    id serial NOT NULL,
    nome text NOT NULL,
    valor text NOT NULL,
    qtd text NOT NULL,
    PRIMARY KEY (id)
);

END;
