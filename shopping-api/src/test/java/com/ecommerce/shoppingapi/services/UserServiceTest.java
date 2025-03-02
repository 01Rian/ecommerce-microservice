package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.user.UserResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String CPF_VALIDO = "12345678900";
    private static final Long ID_USUARIO = 1L;
    private static final String NOME_USUARIO = "Test User";
    private static final String EMAIL_USUARIO = "test@example.com";
    private static final String TELEFONE_USUARIO = "1234567890";
    private static final String ENDERECO_USUARIO = "Test Address";
    private static final String ERRO_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    private static final String API_ERROR_MESSAGE = "API Error";
    private static final String URI_PATH_CPF = "/cpf/";

    @InjectMocks
    private UserService userService;
    
    @Mock
    private WebClient webClientMock;
    
    @Mock
    private WebClient.Builder webClientBuilderMock;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    
    @Mock
    private WebClient.ResponseSpec responseSpecMock;
    
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar o mock do WebClient.Builder
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        
        // Injetar o WebClient.Builder mockado no serviço usando reflexão
        ReflectionTestUtils.setField(userService, "webClientBuilder", webClientBuilderMock);
    }

    @Nested
    @DisplayName("Testes para o método getUserByCpf")
    class GetUserByCpfTests {

        @Test
        @DisplayName("getUserByCpf_CPF_Válido_RetornaUserResponseDto")
        void getUserByCpf_ValidCpf_ReturnsUserResponseDto() {
            // Arrange            
            UserResponseDto expectedUser = UserResponseDto.builder()
                    .id(ID_USUARIO)
                    .name(NOME_USUARIO)
                    .cpf(CPF_VALIDO)
                    .email(EMAIL_USUARIO)
                    .phone(TELEFONE_USUARIO)
                    .address(ENDERECO_USUARIO)
                    .dataRegister(LocalDateTime.now())
                    .build();

            when(responseSpecMock.bodyToMono(UserResponseDto.class)).thenReturn(Mono.just(expectedUser));

            // Act
            UserResponseDto actualUser = userService.getUserByCpf(CPF_VALIDO);

            // Assert
            assertThat(actualUser).isNotNull();
            assertThat(actualUser.getId()).isEqualTo(ID_USUARIO);
            assertThat(actualUser.getName()).isEqualTo(NOME_USUARIO);
            assertThat(actualUser.getCpf()).isEqualTo(CPF_VALIDO);
            assertThat(actualUser.getEmail()).isEqualTo(EMAIL_USUARIO);
            assertThat(actualUser.getPhone()).isEqualTo(TELEFONE_USUARIO);
            assertThat(actualUser.getAddress()).isEqualTo(ENDERECO_USUARIO);

            verify(webClientMock).get();
            verify(requestHeadersUriSpecMock).uri(URI_PATH_CPF + CPF_VALIDO);
            verify(requestHeadersSpecMock).retrieve();
            verify(responseSpecMock).bodyToMono(UserResponseDto.class);
        }

        @Test
        @DisplayName("getUserByCpf_Erro_WebClient_LançaResourceNotFoundException")
        void getUserByCpf_WebClientError_ThrowsResourceNotFoundException() {
            // Arrange
            Exception webClientException = new RuntimeException(API_ERROR_MESSAGE);

            when(responseSpecMock.bodyToMono(UserResponseDto.class)).thenReturn(Mono.error(webClientException));

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserByCpf(CPF_VALIDO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(ERRO_USUARIO_NAO_ENCONTRADO);

            verify(webClientMock).get();
            verify(requestHeadersUriSpecMock).uri(URI_PATH_CPF + CPF_VALIDO);
            verify(requestHeadersSpecMock).retrieve();
            verify(responseSpecMock).bodyToMono(UserResponseDto.class);
        }

        @Test
        @DisplayName("getUserByCpf_Resposta_Nula_LançaResourceNotFoundException")
        void getUserByCpf_NullResponse_ThrowsResourceNotFoundException() {
            // Arrange
            when(responseSpecMock.bodyToMono(UserResponseDto.class)).thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getUserByCpf(CPF_VALIDO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(ERRO_USUARIO_NAO_ENCONTRADO);
                    
            verify(webClientMock).get();
            verify(requestHeadersUriSpecMock).uri(URI_PATH_CPF + CPF_VALIDO);
            verify(requestHeadersSpecMock).retrieve();
            verify(responseSpecMock).bodyToMono(UserResponseDto.class);
        }
    }
}