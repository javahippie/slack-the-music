# Slack the Music!

A small babashka script to publish your current Apple Music track to a Slack Channel.

Usage:

`bb stm.clj -t <YOUR_APP_TOKEN>`

Before running this, you need to create an application in your Slack Workspace which has the permission `users.profile:write` and use the API-Token of this application. You can do this in [the Slack settings](https://api.slack.com/apps?new_app=1).

## License

Copyright © 2021 Tim Zöller

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.`
