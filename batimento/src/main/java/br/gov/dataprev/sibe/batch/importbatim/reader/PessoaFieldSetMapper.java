package br.gov.dataprev.sibe.batch.importbatim.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import br.gov.dataprev.sibe.batch.importbatim.to.PessoaTO;

/**
 * Classe respons�vel por mapear uma linha do arquivo CSV de pessoas para uma entidade.
 * <p>
 * Utiliza o framework SpringBatch.
 * @author DATAPREV/DIT/DEAT
 */
@Component("pessoaFieldSetMapper")
public class PessoaFieldSetMapper implements FieldSetMapper<PessoaTO> {

    /**
     * {@inheritDoc}
     */
    public PessoaTO mapFieldSet(final FieldSet fs) throws BindException {
        final PessoaTO pessoa = new PessoaTO();
        pessoa.setNome(fs.readString("nome"));
        pessoa.setNit(fs.readString("nit"));
        pessoa.setTituloEleitoral(fs.readString("tituloEleitoral"));
        pessoa.setCandidato(fs.readString("candidato"));
        pessoa.setNomeZonaEleitoral(fs.readString("nomeZonaEleitoral"));
        pessoa.setIdMunicipioPrev(fs.readString("idMunicipioPrev"));
        pessoa.setTipo(fs.readString("tipo"));

        return pessoa;
    }

}
