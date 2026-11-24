package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.UsuarioCadastroDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import br.com.aguaemdia.agua_em_dia_api.repository.UsuarioRepository;
import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import br.com.aguaemdia.agua_em_dia_api.validacao.CpfValidador;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //METODO DE CADASTRO DO USUÁRIO

    @Transactional
    public Usuario cadastrar(UsuarioCadastroDTO dados){

        //Padroniza os dados antes de conferir duplicidade: e-mail e usuário em minúsculas, CPF e telefone só com dígitos
        String email = dados.getEmail().trim().toLowerCase();
        String nomeUsuario = dados.getNomeUsuario().trim().toLowerCase();
        String cpf = CpfValidador.apenasDigitos(dados.getCpf());
        String telefone = CpfValidador.apenasDigitos(dados.getTelefone());

        //VERIFICA SE O EMAIL JÁ ESTÁ CADASTRADO
        if(usuarioRepository.existsByEmail(email)){
            throw NegocioException.conflito("Esse e-mail já está cadastrado.");
        }
        //VERIFICA SE O NOME DE USUÁRIO JÁ ESTÁ CADASTRADO
        if(usuarioRepository.existsByNomeUsuario(nomeUsuario)){
            throw NegocioException.conflito("Esse nome de usuário já está em uso.");
        }

        //VERIFICA SE O CPF JÁ ESTÁ CADASTRADO

        if(usuarioRepository.existsByCpf(cpf)){
            throw NegocioException.conflito("Esse CPF já está cadastrado.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dados.getNome().trim());
        usuario.setNomeUsuario(nomeUsuario);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setTermosAceitosEm(Tempo.agora());

        //Criptografa a senha antes de salvar no banco de dados
        usuario.setSenha(passwordEncoder.encode(dados.getSenha()));

        return usuarioRepository.save(usuario);//SALVA O USUÁRIO NO BANCO DE DADOS

    }

    /** Busca o cidadão autenticado. Se a conta não existir mais, a sessão deixa de valer. */
    @Transactional(readOnly = true)
    public Usuario buscar(Long id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> NegocioException.naoAutenticado("Sessão inválida. Entre novamente."));
    }
}
