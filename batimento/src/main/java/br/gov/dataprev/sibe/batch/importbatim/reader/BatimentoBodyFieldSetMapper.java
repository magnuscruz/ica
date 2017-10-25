package br.gov.dataprev.sibe.batch.importbatim.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import br.gov.dataprev.sibe.batch.importbatim.to.BatimentoTO;

/**
 * Classe responsavel por mapear a linha header do arquivo.
 * <p>
 * Utiliza o framework SpringBatch.
 * @author DATAPREV/DIT/DEAT
 */
@Component("batimentoBodyFieldSetMapper")
public class BatimentoBodyFieldSetMapper implements FieldSetMapper<BatimentoTO> {

    /**
     * {@inheritDoc}
     */
    public BatimentoTO mapFieldSet(final FieldSet fs) throws BindException {
        final BatimentoTO header = new BatimentoTO();
        header.setNit(fs.readString("nit"));
        header.setCpf(fs.readString("cpf"));
        header.setNome(fs.readString("nome"));
        header.setNomeMae(fs.readString("nomeMae"));
        header.setDataNascimento(fs.readString("dataNascimento"));
        header.setRg(fs.readString("rg"));
        header.setCtps(fs.readString("ctps"));
        header.setTituloEleitor(fs.readString("tituloEleitor"));
        header.setCnh(fs.readString("cnh"));
        header.setDocEstrageiro(fs.readString("docEstrageiro"));
        header.setCarteiraMaritimo(fs.readString("carteiraMaritimo"));
        header.setPassaporte(fs.readString("passaporte"));
        header.setSexo(fs.readString("sexo"));
        header.setEstadoCivil(fs.readString("estadoCivil"));
        header.setTipoCertidao(fs.readString("tipoCertidao"));
        header.setFolhaCertidao(fs.readString("nit"));
        header.setFolhaCertidao(fs.readString("folhaCertidao"));
        header.setLivroCertidao(fs.readString("livroCertidao"));
        header.setTermoCertidao(fs.readString("termoCertidao"));
        header.setDataLimite(fs.readString("dataLimite"));
        header.setCodigoRetorno(fs.readString("codigoRetorno"));
        header.setCpfCnis(fs.readString("cpfCnis"));
        header.setNomeCpfCnis(fs.readString("nomeCpfCnis"));
        header.setDataNascCnis(fs.readString("dataNascCnis"));
        header.setTotalTrabalhadoresCnis(fs.readString("totalTrabalhadoresCnis"));
        header.setValidacaoCpfReceita(fs.readString("validacaoCpfReceita"));
        header.setDataObito(fs.readString("dataObito"));
        header.setQuantidadeVinculos(fs.readString("quantidadeVinculos"));
        header.setVinculos(fs.readString("vinculos"));
        header.setRemuneracoes(fs.readString("remuneracoes"));
        header.setNumeroBeneficio(fs.readString("numeroBeneficio"));
        header.setTipoEmpregador(fs.readString("tipoEmpregador"));
        header.setIdentificadorEmpregador(fs.readString("identificadorEmpregador"));
        header.setOrgaoLocal(fs.readString("orgaoLocal"));
        header.setOrgaoPagador(fs.readString("orgaoPagador"));
        header.setFonteNit(fs.readString("fonteNit"));
        header.setGrupoEspecies(fs.readString("grupoEspecies"));
        header.setEspecieBeneficio(fs.readString("especieBeneficio"));
        header.setDib(fs.readString("dib"));
        header.setDcb(fs.readString("dcb"));
        header.setCamposFuturos(fs.readString("camposFuturos"));

        return header;
    }

}
