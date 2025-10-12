# Contributing to SafePoint

👍🎉 First off, thanks for taking the time to contribute! 🎉👍

The following is a set of guidelines for contributing to SafePoint. These are mostly guidelines, which we've largely adopted from [Atom](https://github.com/atom/atom/blob/master/CONTRIBUTING.md). Use your good judgment, and feel free to propose changes to this document in a pull request.

## Conduct and licenses

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

Similarly, you agree to share your work under the provisions of the [Mozilla Public License 2.0](LICENSE) (for source code) and the [Creative Commons Attribution Non-Commercial Share-Alike 4.0 International](https://creativecommons.org/licenses/by-nc-sa/4.0/) (for other resources).

Please report unacceptable behaviour to [keiaa.07.05.00@gmail.com](mailto:keiaa.07.05.00@gmail.com).

## What should I know?

### SafePoint

SafePoint is a full-stack web application built with Spring Boot that provides a platform for students to report bullying incidents and schedule one-on-one counseling sessions.

The project was developed primarily as part of a research study exploring how digital tools can support anti-bullying efforts in campus environments. This is specifically tailored to our local institutional context. While the system was designed with real-world use in mind, its current purpose is to serve as a research prototype.

Because we hope this work can inspire or be adapted by other institutions, **this repository intentionally avoids any institution-specific branding, names, logos, or references**. 

If you're contributing, please ensure that your code, documentation, or comments remain generic and reusable. This helps keep the project accessible and relevant to a wider audience beyond our original setting.

### Project conventions

To keep the codebase consistent, readable, and maintainable, please follow these conventions that have been developed over time.

#### Language and tooling

- **Java 17** is the target language version. Please do not use features from newer versions.
- **Maven** is used for dependency management and builds. Avoid adding plugins or dependencies without discussion.
- **Lombok** is enabled project-wide to reduce boilerplate code. However, avoid overusing its annotation in places where explicit control is clearer.
- **Thymeleaf** is used for server-side templating. Write clean, readable markup using its natural syntax and avoid hardcoding values that should be dynamically rendered.

#### Project structure

- Keep concerns separated.
- Follow the standard Spring package layout.
- Maintain modularity of the backend and the frontend.

#### Web layer

- Use `@Controller` (not `@RestController`) since we render HTML templates with Thymeleaf.
- Map routes with semantic, RESTful URL patterns (e.g., `/reports/{id}`, `/counseling/schedule`).
- Prefer `@GetMapping`, `@PostMapping`, etc., over generic `@RequestMapping`.
- Bind path variables with `@PathVariable`, request parameters with `@RequestParam`, and form data with `@ModelAttribute`.

#### Miscellaneous notes

- Manage settings via `application.properties`. Keep environment-specific values out of version control.
- Admin access is controlled via a property-based key system which you have to define. Auth mechanisms should undergo extensive discussion prior introduction.

Other conventions are included in [style guides](#style-guides) below. When in doubt, look at existing code for examples. Consistency across the codebase matters more than personal style!

## How can I contribute?

### Reporting bugs

Before creating bug reports, please check existing ones to avoid duplicates. When you are creating a bug report, please include as many details as possible. When in doubt, refer to the provided template.

You may provide more context by answering these questions:

- **Can you reproduce the problem in an another session or device?**
- **Did the problem start happening recently** or was this always a problem?
- If the problem started happening recently, **can you reproduce the problem in an older version of SafePoint?**
- **Can you reliably reproduce the issue?** If not, provide details about how often the problem happens and under which observed conditions.

If you find a **closed** issue that seems like it is the same thing that you're experiencing, open a new issue and include a link to the original issue in the body of your new one.

#### Suggesting enhancements

Before filing suggestions, please check existing ones to avoid duplicates. When you are filing a suggestion, please include as many details as possible. When in doubt, refer to the provided template.

- Use a clear and descriptive title for the issue to identify the suggestion.
- If possible, provide a step-by-step description of the suggestion in as many details as possible.
- Explain why this enhancement would be useful to most SafePoint users and isn't something that can or should be implemented externally.

#### Pull requests

When you have already wrote a change you want to be added, the next thing to do is to file a pull request.

Please follow these steps when filing one:

1. Follow the instructions in [the template](.github/PULL_REQUEST_TEMPLATE).
2. Follow the [style guides](#style-guides).

While the prerequisites above must be satisfied prior to having your pull request reviewed, the reviewer(s) may ask you to complete additional changes before your pull request can ultimately be accepted.

### Style guides

#### Git commit messages

- Use the present tense ("Add feature" not "Added feature").
- Use the imperative mood ("Move cursor to" not "Moves cursor to").
- When only changing documentation, include [ci skip] in the commit title.
- Consider starting the commit message with an applicable emoji:
    - 🎨 `:art:` when improving the format/structure of the code
    - 🐎 `:racehorse:` when improving performance
    - 📝 `:memo:` when writing docs
    - 🐛 `:bug:` when fixing a bug
    - 🔥 `:fire:` when removing code or files
    - 💚 `:green_heart:` when fixing the CI build
    - ✅ `:white_check_mark:` when adding tests
    - 🔒 `:lock:` when dealing with security
    - ⬆️ `:arrow_up:` when upgrading dependencies

#### Java conventions

- Use **4-space indentation** (no tabs).
- Use K&R style braces: opening and closing brace on same line.
- Use `PascalCase` for classes and interfaces.
- Use `camelCase` for methods and variables.
- Use `UPPER_SNAKE_CASE` for constants and enums.
- Use `lowercase` for packages. No underscores.
- Use explicit imports. No wildcards.
- Organize imports: standard, third-party, then project.
- Break long lines after commas or operators.
- Prefer self-documenting code.
- Use Javadoc for public APIs and complex logic.
- Only one top-level class per file.

#### CSS conventions

- Use **4-space indentation** (no tabs).
- Group related rules with clear section comments.
- Use descriptive, semantic class names.
- Never include institution-specific branding or colors.

### Issue and pull request labels

This section lists the labels we use to help us track and manage issues and pull requests. You are stricly advised to use them properly.

The labels are loosely grouped by their purpose, but it's not required that every issue has a label from every group or that an issue can't have more than one label. Please open an issue if you have suggestions for new labels.

#### Type of issue and issue state

| Label name    	| Description                                        	|
|---------------	|----------------------------------------------------	|
| `bug`         	| Something isn't working                            	|
| `enhancement` 	| New feature or request                             	|
| `help-wanted` 	| Issues that we would appreciate help resolving     	|
| `duplicate`   	| Issues which are duplicates of others              	|
| `wontfix`     	| Issues that the maintainers decided not to address 	|
| `invalid`     	| Issues which aren't valid from our point of view   	|

#### Type of pull request and state

| Label name         	| Description                                                                              	|
|--------------------	|------------------------------------------------------------------------------------------	|
| `work-in-progress` 	| Pull requests which are still being worked on, more changes will follow                 	|
| `needs-review`     	| Pull requests which need code review and approval from maintainers                      	|
| `under-review`     	| Pull requests being reviewed by maintainers                                             	|
| `requires-changes` 	| Pull requests which need to be updated based on review comments and then reviewed again 	|
| `needs-testing`    	| Pull requests which need manual testing                                                 	|