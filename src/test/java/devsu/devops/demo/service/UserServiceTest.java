package devsu.devops.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import devsu.devops.demo.entity.User;
import devsu.devops.demo.exception.ResourceNotFoundException;
import devsu.devops.demo.repository.UserRepository;
import devsu.devops.demo.service.dto.UserDto;
import devsu.devops.demo.service.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Test
	@DisplayName("findAll - Empty")
	void findAll_Empty() {
		when(userRepository.findAll()).thenReturn(new ArrayList<>());
		assertEquals(userService.findAll().isEmpty(), true);
	}

	@Test
	@DisplayName("findAll - Maps users")
	void findAll_MapsUsers() {
		User user = User.builder().id(1L).name("Test User").dni("0999999999").build();
		UserDto userDto = UserDto.builder().id(1L).name("Test User").dni("0999999999").build();
		when(userRepository.findAll()).thenReturn(java.util.List.of(user));
		when(userMapper.toDto(user)).thenReturn(userDto);

		assertThat(userService.findAll()).containsExactly(userDto);
	}

	@Test
	@DisplayName("findById - Not null")
	void findById_NotNull() {
		Optional<User> user = Optional.of(User.builder()
				.id((long) 1)
				.name("Test")
				.dni("0999999999")
				.build());

		UserDto userDto = UserDto.builder()
				.id((long) 1)
				.name("Test")
				.dni("0999999999")
				.build();

		when(userRepository.findById((long) 1)).thenReturn(user);
		when(userMapper.toDto(user.get())).thenReturn(userDto);
		assertThat(userService.findById((long) 1).getName())
			.isEqualTo(user.get().getName());
	}

	@Test()
	@DisplayName("findById - Null")
	void findById_Null() {
		when(userRepository.findById((long) 1)).thenReturn(Optional.empty());
		Throwable exception = assertThrows(ResourceNotFoundException.class, () -> userService.findById((long) 1));
		assertEquals("User not found: 1", exception.getMessage());
	}

	@Test
	@DisplayName("save - New user")
	void save_NewUser() {
		UserDto input = UserDto.builder().name("Test User").dni("0999999999").build();
		User entity = User.builder().name("Test User").dni("0999999999").build();
		User persisted = User.builder().id(1L).name("Test User").dni("0999999999").build();
		UserDto output = UserDto.builder().id(1L).name("Test User").dni("0999999999").build();
		when(userRepository.existsByDni(input.getDni())).thenReturn(false);
		when(userMapper.toEntity(input)).thenReturn(entity);
		when(userRepository.save(entity)).thenReturn(persisted);
		when(userMapper.toDto(persisted)).thenReturn(output);

		assertThat(userService.save(input)).isEqualTo(output);
	}

	@Test
	@DisplayName("save - Duplicate DNI")
	void save_DuplicateDni() {
		UserDto input = UserDto.builder().name("Test User").dni("0999999999").build();
		when(userRepository.existsByDni(input.getDni())).thenReturn(true);

		Throwable exception = assertThrows(
				devsu.devops.demo.exception.BadRequestException.class,
				() -> userService.save(input));
		assertEquals("User already exists", exception.getMessage());
	}
}
